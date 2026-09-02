package services

import javax.inject._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt

trait PasswordService {
  def hash(password: String): Future[String]
  def check(password: String, hash: String): Future[Boolean]
}

@Singleton
class BCryptPasswordService @Inject()() extends PasswordService {
  override def hash(password: String): Future[String] = {
    Future.successful(BCrypt.hashpw(password, BCrypt.gensalt()))
  }

  override def check(password: String, hash: String): Future[Boolean] = {
    Future.successful(BCrypt.checkpw(password, hash))
  }
}
