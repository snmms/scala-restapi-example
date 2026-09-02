package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.AuthenticatedUser
import repositories.UserRepository

trait AuthService {
  def validateToken(token: String): Future[Option[AuthenticatedUser]]
}

@Singleton
class AuthServiceImpl @Inject()(
  userRepository: UserRepository
)(implicit ec: ExecutionContext) extends AuthService {

  override def validateToken(token: String): Future[Option[AuthenticatedUser]] = {
    userRepository.findAll(0, Int.MaxValue).map { users =>
      users.find(u => u.id.toString == token).map { user =>
        AuthenticatedUser(user.id, user.email, user.name, user.role)
      }
    }
  }
}
