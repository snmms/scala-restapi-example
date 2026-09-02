// app/controllers/HealthController.scala
package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import java.time.Instant

@Singleton
class HealthController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  private val startTime = Instant.now()

  def check(): Action[AnyContent] = Action {
    Ok(Json.obj("status" -> "healthy", "timestamp" -> Instant.now().toString))
  }
}
