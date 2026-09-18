package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models._
import db.DatabaseManager

trait VentaRepository {
  def findAll(): Future[Seq[Venta]]
  def findById(id: Long): Future[Option[(Venta, Seq[DetalleVenta])]]
  def create(items: Seq[(Long, Int)]): Future[Venta]
  def findBestSellers(limit: Int): Future[Seq[(Producto, Int)]]
}

@Singleton
class VentaRepositoryImpl @Inject()(
  dbManager: DatabaseManager
)(implicit ec: ExecutionContext) extends VentaRepository {

  private def rsToVenta(rs: java.sql.ResultSet): Venta = Venta(
    rs.getLong("id_venta"),
    rs.getString("fecha"),
    rs.getDouble("total")
  )

  private def rsToDetalle(rs: java.sql.ResultSet): DetalleVenta = DetalleVenta(
    rs.getLong("id_detalle"),
    rs.getLong("id_venta"),
    rs.getLong("id_producto"),
    rs.getInt("cantidad"),
    rs.getDouble("precio_unitario")
  )

  override def findAll(): Future[Seq[Venta]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement("SELECT * FROM ventas ORDER BY fecha DESC")
      val rs = stmt.executeQuery()
      try { Iterator.continually(rs).takeWhile(_.next()).map(rsToVenta).toSeq }
      finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }

  override def findById(id: Long): Future[Option[(Venta, Seq[DetalleVenta])]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement("SELECT * FROM ventas WHERE id_venta = ?")
      stmt.setLong(1, id)
      val rs = stmt.executeQuery()
      try {
        if (rs.next()) {
          val venta = rsToVenta(rs)
          val detStmt = conn.prepareStatement("SELECT * FROM detalle_ventas WHERE id_venta = ?")
          detStmt.setLong(1, id)
          val detRs = detStmt.executeQuery()
          try {
            val detalles = Iterator.continually(detRs).takeWhile(_.next()).map(rsToDetalle).toSeq
            Some((venta, detalles))
          } finally { try { detRs.close() } catch { case _: Exception => }; try { detStmt.close() } catch { case _: Exception => } }
        } else None
      } finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }

  override def create(items: Seq[(Long, Int)]): Future[Venta] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      conn.setAutoCommit(false)
    try {
      var total = 0.0
      val detalles = items.map { case (idProducto, cantidad) =>
        val stmt = conn.prepareStatement("SELECT precio FROM productos WHERE id_producto = ?")
        stmt.setLong(1, idProducto)
        val rs = stmt.executeQuery()
        if (rs.next()) {
          val precio = rs.getDouble("precio")
          total += precio * cantidad
          (idProducto, cantidad, precio)
        } else throw new RuntimeException(s"Producto $idProducto no encontrado")
      }

      val ventaStmt = conn.prepareStatement(
        "INSERT INTO ventas (total) VALUES (?)", java.sql.Statement.RETURN_GENERATED_KEYS
      )
      ventaStmt.setDouble(1, total)
      ventaStmt.executeUpdate()
      val ventaKeys = ventaStmt.getGeneratedKeys
      ventaKeys.next()
      val idVenta = ventaKeys.getLong(1)

      val fechaStmt = conn.prepareStatement("SELECT fecha FROM ventas WHERE id_venta = ?")
      fechaStmt.setLong(1, idVenta)
      val fechaRs = fechaStmt.executeQuery()
      fechaRs.next()
      val fecha = fechaRs.getString("fecha")

      detalles.foreach { case (idProducto, cantidad, precio) =>
        val detStmt = conn.prepareStatement(
          "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)"
        )
        detStmt.setLong(1, idVenta)
        detStmt.setLong(2, idProducto)
        detStmt.setInt(3, cantidad)
        detStmt.setDouble(4, precio)
        detStmt.executeUpdate()

        val updStmt = conn.prepareStatement("UPDATE productos SET stock = stock - ? WHERE id_producto = ?")
        updStmt.setInt(1, cantidad)
        updStmt.setLong(2, idProducto)
        updStmt.executeUpdate()
      }

      conn.commit()
      Venta(idVenta, fecha, total)
    } catch {
      case e: Exception =>
        conn.rollback()
        throw e
    } finally {
      conn.setAutoCommit(true)
    }
    }
  }

  override def findBestSellers(limit: Int): Future[Seq[(Producto, Int)]] = Future {
    val conn = dbManager.getConnection
    conn.synchronized {
      val stmt = conn.prepareStatement(
        """SELECT p.*, SUM(dv.cantidad) as total_vendido
           FROM productos p
           JOIN detalle_ventas dv ON p.id_producto = dv.id_producto
           GROUP BY p.id_producto
           ORDER BY total_vendido DESC
           LIMIT ?"""
      )
      stmt.setInt(1, limit)
      val rs = stmt.executeQuery()
      try {
        Iterator.continually(rs).takeWhile(_.next()).map { rs =>
          val producto = Producto(
            rs.getLong("id_producto"),
            rs.getString("nombre"),
            Option(rs.getString("descripcion")),
            rs.getDouble("precio"),
            rs.getInt("stock"),
            rs.getInt("stock_minimo"),
            rs.getLong("id_categoria")
          )
          (producto, rs.getInt("total_vendido"))
        }.toSeq
      } finally { try { rs.close() } catch { case _: Exception => }; try { stmt.close() } catch { case _: Exception => } }
    }
  }
}
