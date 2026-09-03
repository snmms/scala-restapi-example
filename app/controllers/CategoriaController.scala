package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.CategoriaService

@Singleton
class CategoriaController @Inject()(
  val controllerComponents: ControllerComponents,
  categoriaService: CategoriaService
)(implicit ec: ExecutionContext) extends BaseController {

  def list(): Action[AnyContent] = Action.async {
    categoriaService.list().map { categorias =>
      Ok(Json.toJson(categorias))
    }
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    categoriaService.get(id).map {
      case Some(categoria) => Ok(Json.toJson(categoria))
      case None => NotFound(Json.obj("error" -> "Categoría no encontrada", "id" -> id))
    }
  }
}
