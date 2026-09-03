package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.ProductoService
import models.CreateProductoRequest

@Singleton
class ProductoController @Inject()(
  val controllerComponents: ControllerComponents,
  productoService: ProductoService
)(implicit ec: ExecutionContext) extends BaseController {

  def list(categoria: Long, precioMin: Double, precioMax: Double,
           bajoStock: Boolean, disponible: Boolean, ordenar: String): Action[AnyContent] = Action.async {
    val cat = if (categoria == 0) None else Some(categoria)
    val pMin = if (precioMin == 0.0) None else Some(precioMin)
    val pMax = if (precioMax == 0.0) None else Some(precioMax)
    productoService.list(cat, pMin, pMax, bajoStock, disponible, ordenar).map { productos =>
      Ok(Json.toJson(productos))
    }
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    productoService.get(id).map {
      case Some(producto) => Ok(Json.toJson(producto))
      case None => NotFound(Json.obj("error" -> "Producto no encontrado", "id" -> id))
    }
  }

  def byCategoria(idCategoria: Long): Action[AnyContent] = Action.async {
    productoService.byCategoria(idCategoria).map { productos =>
      Ok(Json.toJson(productos))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateProductoRequest] match {
      case JsSuccess(req, _) =>
        productoService.create(
          req.nombre, req.descripcion, req.precio, req.stock, req.stock_minimo, req.id_categoria
        ).map { producto =>
          Created(Json.toJson(producto))
            .withHeaders("Location" -> s"/api/v1/productos/${producto.id_producto}")
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "error" -> "Datos inválidos",
          "details" -> JsError.toJson(errors)
        )))
    }
  }

  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[models.UpdateProductoRequest] match {
      case JsSuccess(req, _) =>
        productoService.update(
          id, req.nombre, req.descripcion, req.precio, req.stock, req.stock_minimo, req.id_categoria
        ).map {
          case Some(producto) => Ok(Json.toJson(producto))
          case None => NotFound(Json.obj("error" -> "Producto no encontrado"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("error" -> "Datos inválidos")))
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    productoService.delete(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("error" -> "Producto no encontrado"))
    }
  }
}
