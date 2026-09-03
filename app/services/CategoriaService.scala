package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Categoria
import repositories.CategoriaRepository

trait CategoriaService {
  def list(): Future[Seq[Categoria]]
  def get(id: Long): Future[Option[Categoria]]
}

@Singleton
class CategoriaServiceImpl @Inject()(
  repository: CategoriaRepository
)(implicit ec: ExecutionContext) extends CategoriaService {

  override def list(): Future[Seq[Categoria]] = repository.findAll()
  override def get(id: Long): Future[Option[Categoria]] = repository.findById(id)
}
