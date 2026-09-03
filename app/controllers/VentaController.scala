package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.VentaService
import models.CreateVentaRequest

@Singleton
class VentaController @Inject()(
  val controllerComponents: ControllerComponents,
  ventaService: VentaService
)(implicit ec: ExecutionContext) extends BaseController {

  implicit val detalleVentaWrites: Writes[models.DetalleVenta] = Json.writes[models.DetalleVenta]
  implicit val ventaConDetalleWrites: Writes[(models.Venta, Seq[models.DetalleVenta])] = new Writes[(models.Venta, Seq[models.DetalleVenta])] {
    def writes(tuple: (models.Venta, Seq[models.DetalleVenta])): JsValue = Json.obj(
      "venta" -> Json.toJson(tuple._1),
      "detalles" -> Json.toJson(tuple._2)
    )
  }
  implicit val productoMasVendidoWrites: Writes[(models.Producto, Int)] = new Writes[(models.Producto, Int)] {
    def writes(tuple: (models.Producto, Int)): JsValue = Json.obj(
      "producto" -> Json.toJson(tuple._1),
      "total_vendido" -> tuple._2
    )
  }

  def list(): Action[AnyContent] = Action.async {
    ventaService.list().map { ventas =>
      Ok(Json.toJson(ventas))
    }
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    ventaService.get(id).map {
      case Some(ventaConDetalles) => Ok(Json.toJson(ventaConDetalles))
      case None => NotFound(Json.obj("error" -> "Venta no encontrada", "id" -> id))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateVentaRequest] match {
      case JsSuccess(req, _) =>
        val items = req.items.map(i => (i.id_producto, i.cantidad))
        ventaService.create(items).map { venta =>
          Created(Json.toJson(venta))
        }.recover {
          case e: RuntimeException =>
            BadRequest(Json.obj("error" -> e.getMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "error" -> "Datos inválidos",
          "details" -> JsError.toJson(errors)
        )))
    }
  }

  def bestSellers(): Action[AnyContent] = Action.async {
    ventaService.bestSellers(10).map { productos =>
      Ok(Json.toJson(productos))
    }
  }
}
