package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.UserService
import models.{User, CreateUserRequest, UpdateUserRequest}

@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action {
    Ok(Json.obj("message" -> "User API", "version" -> "1.0"))
  }
}

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  userService: UserService
)(implicit ec: ExecutionContext) extends BaseController {

  // GET /api/v1/users
  def list(page: Int, size: Int): Action[AnyContent] = Action.async {
    if (page < 1 || size < 1 || size > 100) {
      Future.successful(BadRequest(Json.obj(
        "error" -> "Invalid pagination parameters"
      )))
    } else {
      userService.listUsers(page, size).map { users =>
        Ok(Json.toJson(users))
      }
    }
  }

  // GET /api/v1/users/:id
  def get(id: Long): Action[AnyContent] = Action.async {
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("error" -> "User not found", "id" -> id))
    }
  }

  // POST /api/v1/users
  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateUserRequest] match {
      case JsSuccess(createRequest, _) =>
        userService.createUser(createRequest).map { user =>
          Created(Json.toJson(user))
            .withHeaders("Location" -> s"/api/v1/users/${user.id}")
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "error" -> "Invalid request body",
          "details" -> JsError.toJson(errors)
        )))
    }
  }

  // PUT /api/v1/users/:id
  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateUserRequest] match {
      case JsSuccess(updateRequest, _) =>
        userService.updateUser(id, updateRequest).map {
          case Some(user) => Ok(Json.toJson(user))
          case None => NotFound(Json.obj("error" -> "User not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("error" -> "Invalid request")))
    }
  }

  // DELETE /api/v1/users/:id
  def delete(id: Long): Action[AnyContent] = Action.async {
    userService.deleteUser(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("error" -> "User not found"))
    }
  }

  // GET /api/v1/users/search
  def search(q: String, limit: Int): Action[AnyContent] = Action.async {
    userService.listUsers(1, limit).map { users =>
      val filtered = users.filter(u =>
        u.name.toLowerCase.contains(q.toLowerCase) ||
        u.email.toLowerCase.contains(q.toLowerCase)
      )
      Ok(Json.toJson(filtered))
    }
  }
}
