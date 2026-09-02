package services

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.{User, CreateUserRequest, UpdateUserRequest}
import repositories.UserRepository

trait UserService {
  def listUsers(page: Int, size: Int): Future[Seq[User]]
  def getUser(id: Long): Future[Option[User]]
  def createUser(request: CreateUserRequest): Future[User]
  def updateUser(id: Long, request: UpdateUserRequest): Future[Option[User]]
  def deleteUser(id: Long): Future[Boolean]
}

@Singleton
class UserServiceImpl @Inject()(
  userRepository: UserRepository,
  passwordService: PasswordService
)(implicit ec: ExecutionContext) extends UserService {

  override def listUsers(page: Int, size: Int): Future[Seq[User]] = {
    val offset = (page - 1) * size
    userRepository.findAll(offset, size)
  }

  override def getUser(id: Long): Future[Option[User]] = {
    userRepository.findById(id)
  }

  override def createUser(request: CreateUserRequest): Future[User] = {
    for {
      hashedPassword <- passwordService.hash(request.password)
      user <- userRepository.create(
        request.email,
        request.name,
        hashedPassword,
        request.role
      )
    } yield user
  }

  override def updateUser(id: Long, request: UpdateUserRequest): Future[Option[User]] = {
    userRepository.findById(id).flatMap {
      case Some(existing) =>
        val updated = existing.copy(
          email = request.email.getOrElse(existing.email),
          name = request.name.getOrElse(existing.name),
          role = request.role.getOrElse(existing.role)
        )
        userRepository.update(updated).map(Some(_))
      case None => Future.successful(None)
    }
  }

  override def deleteUser(id: Long): Future[Boolean] = {
    userRepository.delete(id)
  }
}
