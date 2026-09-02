package controllers.actions

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import services.AuthService
import models.AuthenticatedUser

class AuthenticatedRequest[A](
  val user: AuthenticatedUser,
  request: Request[A]
) extends WrappedRequest[A](request)

@Singleton
class AuthenticatedAction @Inject()(
  val parser: BodyParsers.Default,
  authService: AuthService
)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent]
  with ActionRefiner[Request, AuthenticatedRequest] {

  override protected def refine[A](
    request: Request[A]
  ): Future[Either[Result, AuthenticatedRequest[A]]] = {

    val tokenOpt = request.headers
      .get("Authorization")
      .filter(_.startsWith("Bearer "))
      .map(_.stripPrefix("Bearer "))

    tokenOpt match {
      case Some(token) =>
        authService.validateToken(token).map {
          case Some(user) => Right(new AuthenticatedRequest(user, request))
          case None => Left(Results.Unauthorized(Json.obj("error" -> "Invalid token")))
        }
      case None =>
        Future.successful(Left(Results.Unauthorized(Json.obj(
          "error" -> "Missing Authorization header"
        ))))
    }
  }
}
