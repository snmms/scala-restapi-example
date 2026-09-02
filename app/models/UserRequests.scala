package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CreateUserRequest(
  email: String,
  name: String,
  password: String,
  role: UserRole = UserRole.Viewer
)

object CreateUserRequest {
  implicit val reads: Reads[CreateUserRequest] = (
    (__ \ "email").read[String](Reads.email) and
    (__ \ "name").read[String](Reads.minLength[String](2)) and
    (__ \ "password").read[String](Reads.minLength[String](8)) and
    (__ \ "role").readWithDefault[UserRole](UserRole.Viewer)
  )(CreateUserRequest.apply _)
}

case class UpdateUserRequest(
  email: Option[String],
  name: Option[String],
  role: Option[UserRole]
)

object UpdateUserRequest {
  implicit val reads: Reads[UpdateUserRequest] = (
    (__ \ "email").readNullable[String](Reads.email) and
    (__ \ "name").readNullable[String](Reads.minLength[String](2)) and
    (__ \ "role").readNullable[UserRole]
  )(UpdateUserRequest.apply _)
}
