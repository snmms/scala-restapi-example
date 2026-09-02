// app/handlers/CustomErrorHandler.scala
package handlers

import javax.inject._
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.Logger
import scala.concurrent.Future

@Singleton
class CustomErrorHandler @Inject()() extends HttpErrorHandler {

  private val logger = Logger(this.getClass)

  override def onClientError(
    request: RequestHeader, statusCode: Int, message: String
  ): Future[Result] = {
    val errorResponse = statusCode match {
      case 400 => Json.obj("error" -> "Bad Request", "code" -> "BAD_REQUEST")
      case 401 => Json.obj("error" -> "Unauthorized", "code" -> "UNAUTHORIZED")
      case 404 => Json.obj("error" -> "Not Found", "code" -> "NOT_FOUND")
      case _ => Json.obj("error" -> "Client Error", "code" -> s"ERROR_$statusCode")
    }
    Future.successful(Status(statusCode)(errorResponse))
  }

  override def onServerError(
    request: RequestHeader, exception: Throwable
  ): Future[Result] = {
    logger.error(s"Server error on ${request.method} ${request.path}", exception)
    Future.successful(InternalServerError(Json.obj(
      "error" -> "Internal Server Error",
      "code" -> "INTERNAL_ERROR"
    )))
  }
}
