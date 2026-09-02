package models

case class AuthenticatedUser(
  id: Long,
  email: String,
  name: String,
  role: UserRole
)
