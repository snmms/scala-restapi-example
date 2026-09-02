package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.time.Instant

case class User(
  id: Long,
  email: String,
  name: String,
  role: UserRole,
  createdAt: Instant,
  updatedAt: Instant
)

sealed trait UserRole
object UserRole {
  case object Admin extends UserRole
  case object Editor extends UserRole
  case object Viewer extends UserRole

  implicit val format: Format[UserRole] = new Format[UserRole] {
    override def reads(json: JsValue): JsResult[UserRole] = json match {
      case JsString("admin") => JsSuccess(Admin)
      case JsString("editor") => JsSuccess(Editor)
      case JsString("viewer") => JsSuccess(Viewer)
      case _ => JsError("Invalid user role")
    }

    override def writes(role: UserRole): JsValue = role match {
      case Admin => JsString("admin")
      case Editor => JsString("editor")
      case Viewer => JsString("viewer")
    }
  }
}

object User {
  implicit val format: Format[User] = Json.format[User]
}
