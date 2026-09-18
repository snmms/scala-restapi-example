package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Producto
import db.DatabaseManager

trait ProductoRepository {
  def findAll(categoria: Option[Long], precioMin: Option[Double], precioMax: Option[Double],
              bajoStock: Boolean, disponible: Boolean, ordenar: String): Future[Seq[Producto]]
  def findById(id: Long): Future[Option[Producto]]
  def findByCategoria(idCategoria: Long): Future[Seq[Producto]]
  def create(nombre: String, descripcion: Option[String], precio: Double,
             stock: Int, stockMinimo: Int, idCategoria: Long): Future[Producto]
  def update(id: Long, nombre: Option[String], descripcion: Option[String],
             precio: Option[Double], stock: Option[Int], stockMinimo: Option[Int],
             idCategoria: Option[Long]): Future[Option[Producto]]
  def delete(id: Long): Future[Boolean]
}

@Singleton
class ProductoRepositoryImpl @Inject()(
  dbManager: DatabaseManager
)(implicit ec: ExecutionContext) extends ProductoRepository {

  private def rsToProducto(rs: java.sql.ResultSet): Producto = Producto(
    rs.getLong("id_producto"),
    rs.getString("nombre"),
    Option(rs.getString("descripcion")),
    rs.getDouble("precio"),
    rs.getInt("stock"),
    rs.getInt("stock_minimo"),
    rs.getLong("id_categoria")
  )

  override def findAll(categoria: Option[Long], precioMin: Option[Double], precioMax: Option[Double],
                       bajoStock: Boolean, disponible: Boolean, ordenar: String): Future[Seq[Producto]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val conditions = scala.collection.mutable.ListBuffer[String]()
      val params = scala.collection.mutable.ListBuffer[Any]()

      categoria.foreach { c =>
        conditions += "id_categoria = ?"
        params += c
      }
      precioMin.foreach { p =>
        conditions += "precio >= ?"
        params += p
      }
      precioMax.foreach { p =>
        conditions += "precio <= ?"
        params += p
      }
      if (bajoStock) conditions += "stock <= stock_minimo"
      if (disponible) conditions += "stock > 0"

      val where = if (conditions.nonEmpty) " WHERE " + conditions.mkString(" AND ") else ""

      val orderClause = ordenar match {
        case "precio" => " ORDER BY precio ASC"
        case "precio_desc" => " ORDER BY precio DESC"
        case "stock" => " ORDER BY stock ASC"
        case "stock_desc" => " ORDER BY stock DESC"
        case "nombre" => " ORDER BY nombre ASC"
        case _ => " ORDER BY id_producto ASC"
      }

      val stmt = conn.prepareStatement(s"SELECT * FROM productos$where$orderClause")
      params.zipWithIndex.foreach { case (p, i) =>
        p match {
          case l: Long => stmt.setLong(i + 1, l)
          case d: Double => stmt.setDouble(i + 1, d)
          case _ =>
        }
      }
      val rs = stmt.executeQuery()
      try { Iterator.continually(rs).takeWhile(_.next()).map(rsToProducto).toSeq }
      finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }

  override def findById(id: Long): Future[Option[Producto]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement("SELECT * FROM productos WHERE id_producto = ?")
      //val demo= conn.prepareStatement("select * from productos")
      stmt.setLong(1, id)
      val rs = stmt.executeQuery()
      try { if (rs.next()) Some(rsToProducto(rs)) else None }
      finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }

  override def findByCategoria(idCategoria: Long): Future[Seq[Producto]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement("SELECT * FROM productos WHERE id_categoria = ? ORDER BY nombre")
      stmt.setLong(1, idCategoria)
      val rs = stmt.executeQuery()
      try { Iterator.continually(rs).takeWhile(_.next()).map(rsToProducto).toSeq }
      finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }

  override def create(nombre: String, descripcion: Option[String], precio: Double,
                      stock: Int, stockMinimo: Int, idCategoria: Long): Future[Producto] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement(
        "INSERT INTO productos (nombre, descripcion, precio, stock, stock_minimo, id_categoria) VALUES (?, ?, ?, ?, ?, ?)",
        java.sql.Statement.RETURN_GENERATED_KEYS
      )
      stmt.setString(1, nombre)
      stmt.setString(2, descripcion.orNull)
      stmt.setDouble(3, precio)
      stmt.setInt(4, stock)
      stmt.setInt(5, stockMinimo)
      stmt.setLong(6, idCategoria)
      stmt.executeUpdate()
      val keys = stmt.getGeneratedKeys
      keys.next()
      Producto(keys.getLong(1), nombre, descripcion, precio, stock, stockMinimo, idCategoria)
    }
  }

  override def update(id: Long, nombre: Option[String], descripcion: Option[String],
                      precio: Option[Double], stock: Option[Int], stockMinimo: Option[Int],
                      idCategoria: Option[Long]): Future[Option[Producto]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val sel = conn.prepareStatement("SELECT * FROM productos WHERE id_producto = ?")
      sel.setLong(1, id)
      val rs = sel.executeQuery()
      val result = if (!rs.next()) None
      else {
        val p = rsToProducto(rs)
        rs.close()
        sel.close()
        val newNombre = nombre.getOrElse(p.nombre)
        val newDesc: Option[String] = descripcion match {
          case Some(v) => Some(v)
          case None => p.descripcion
        }
        val newPrecio = precio.getOrElse(p.precio)
        val newStock = stock.getOrElse(p.stock)
        val newStockMin = stockMinimo.getOrElse(p.stock_minimo)
        val newCatId = idCategoria.getOrElse(p.id_categoria)

        val stmt = conn.prepareStatement(
          "UPDATE productos SET nombre=?, descripcion=?, precio=?, stock=?, stock_minimo=?, id_categoria=? WHERE id_producto=?"
        )
        stmt.setString(1, newNombre)
        stmt.setString(2, newDesc.orNull)
        stmt.setDouble(3, newPrecio)
        stmt.setInt(4, newStock)
        stmt.setInt(5, newStockMin)
        stmt.setLong(6, newCatId)
        stmt.setLong(7, id)
        val updated = stmt.executeUpdate()
        stmt.close()
        if (updated > 0) Some(p.copy(nombre = newNombre, descripcion = newDesc, precio = newPrecio,
          stock = newStock, stock_minimo = newStockMin, id_categoria = newCatId))
        else None
      }
      try { if (!rs.isClosed) rs.close() } catch { case _: Exception => }
      try { if (!sel.isClosed) sel.close() } catch { case _: Exception => }
      result
    }
  }

  override def delete(id: Long): Future[Boolean] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement("DELETE FROM productos WHERE id_producto = ?")
      stmt.setLong(1, id)
      stmt.executeUpdate() > 0
    }
  }
}
