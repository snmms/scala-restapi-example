package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.Producto
import repositories.ProductoRepository

trait ProductoService {
  def list(categoria: Option[Long], precioMin: Option[Double], precioMax: Option[Double],
           bajoStock: Boolean, disponible: Boolean, ordenar: String): Future[Seq[Producto]]
  def get(id: Long): Future[Option[Producto]]
  def byCategoria(idCategoria: Long): Future[Seq[Producto]]
  def create(nombre: String, descripcion: Option[String], precio: Double,
             stock: Int, stockMinimo: Int, idCategoria: Long): Future[Producto]
  def update(id: Long, nombre: Option[String], descripcion: Option[String],
             precio: Option[Double], stock: Option[Int], stockMinimo: Option[Int],
             idCategoria: Option[Long]): Future[Option[Producto]]
  def delete(id: Long): Future[Boolean]
}

@Singleton
class ProductoServiceImpl @Inject()(
  repository: ProductoRepository
)(implicit ec: ExecutionContext) extends ProductoService {

  override def list(categoria: Option[Long], precioMin: Option[Double], precioMax: Option[Double],
                    bajoStock: Boolean, disponible: Boolean, ordenar: String): Future[Seq[Producto]] =
    repository.findAll(categoria, precioMin, precioMax, bajoStock, disponible, ordenar)

  override def get(id: Long): Future[Option[Producto]] = repository.findById(id)
  override def byCategoria(idCategoria: Long): Future[Seq[Producto]] = repository.findByCategoria(idCategoria)

  override def create(nombre: String, descripcion: Option[String], precio: Double,
                      stock: Int, stockMinimo: Int, idCategoria: Long): Future[Producto] =
    repository.create(nombre, descripcion, precio, stock, stockMinimo, idCategoria)

  override def update(id: Long, nombre: Option[String], descripcion: Option[String],
                      precio: Option[Double], stock: Option[Int], stockMinimo: Option[Int],
                      idCategoria: Option[Long]): Future[Option[Producto]] =
    repository.update(id, nombre, descripcion, precio, stock, stockMinimo, idCategoria)

  override def delete(id: Long): Future[Boolean] = repository.delete(id)
}
