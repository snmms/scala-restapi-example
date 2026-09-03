package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Categoria
import db.DatabaseManager

trait CategoriaRepository {
  def findAll(): Future[Seq[Categoria]]
  def findById(id: Long): Future[Option[Categoria]]
}

@Singleton
class CategoriaRepositoryImpl @Inject()(
  dbManager: DatabaseManager
)(implicit ec: ExecutionContext) extends CategoriaRepository {

  override def findAll(): Future[Seq[Categoria]] = Future {
    val conn = dbManager.getConnection
    val stmt = conn.prepareStatement("SELECT * FROM categorias ORDER BY nombre")
    val rs = stmt.executeQuery()
    Iterator.continually(rs).takeWhile(_.next()).map { rs =>
      Categoria(
        rs.getLong("id_categoria"),
        rs.getString("nombre"),
        Option(rs.getString("descripcion"))
      )
    }.toSeq
  }

  override def findById(id: Long): Future[Option[Categoria]] = Future {
    val conn = dbManager.getConnection
    val stmt = conn.prepareStatement("SELECT * FROM categorias WHERE id_categoria = ?")
    stmt.setLong(1, id)
    val rs = stmt.executeQuery()
    if (rs.next()) {
      Some(Categoria(
        rs.getLong("id_categoria"),
        rs.getString("nombre"),
        Option(rs.getString("descripcion"))
      ))
    } else None
  }
}
