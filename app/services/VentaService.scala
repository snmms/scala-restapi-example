package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models._
import repositories.VentaRepository

trait VentaService {
  def list(): Future[Seq[Venta]]
  def get(id: Long): Future[Option[(Venta, Seq[DetalleVenta])]]
  def create(items: Seq[(Long, Int)]): Future[Venta]
  def bestSellers(limit: Int): Future[Seq[(Producto, Int)]]
}

@Singleton
class VentaServiceImpl @Inject()(
  repository: VentaRepository
)(implicit ec: ExecutionContext) extends VentaService {

  override def list(): Future[Seq[Venta]] = repository.findAll()
  override def get(id: Long): Future[Option[(Venta, Seq[DetalleVenta])]] = repository.findById(id)
  override def create(items: Seq[(Long, Int)]): Future[Venta] = repository.create(items)
  override def bestSellers(limit: Int): Future[Seq[(Producto, Int)]] = repository.findBestSellers(limit)
}
