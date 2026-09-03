package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(Json.obj(
      "message" -> "Tech Products Store API",
      "version" -> "1.0",
      "endpoints" -> Json.arr(
        "GET  /api/v1/categorias",
        "GET  /api/v1/categorias/:id",
        "GET  /api/v1/productos",
        "GET  /api/v1/productos/:id",
        "POST /api/v1/productos",
        "PUT  /api/v1/productos/:id",
        "DELETE /api/v1/productos/:id",
        "GET  /api/v1/productos/categoria/:id",
        "GET  /api/v1/ventas",
        "GET  /api/v1/ventas/:id",
        "POST /api/v1/ventas",
        "GET  /api/v1/ventas/productos-mas-vendidos",
        "GET  /health"
      )
    ))
  }
}
