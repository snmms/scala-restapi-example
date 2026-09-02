package repositories

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import models.{User, UserRole}
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

trait UserRepository {
  def findAll(offset: Int, limit: Int): Future[Seq[User]]
  def findById(id: Long): Future[Option[User]]
  def create(email: String, name: String, passwordHash: String, role: UserRole): Future[User]
  def update(user: User): Future[User]
  def delete(id: Long): Future[Boolean]
}

@Singleton
class UserRepositoryImpl @Inject()(
)(implicit ec: ExecutionContext) extends UserRepository {

  private val users = scala.collection.mutable.ListBuffer[User]()
  private val idCounter = new AtomicLong(1)

  override def findAll(offset: Int, limit: Int): Future[Seq[User]] = Future {
    users.sortBy(_.createdAt)(Ordering[Instant].reverse).drop(offset).take(limit).toSeq
  }

  override def findById(id: Long): Future[Option[User]] = Future {
    users.find(_.id == id)
  }

  override def create(
    email: String, name: String, passwordHash: String, role: UserRole
  ): Future[User] = Future {
    val now = Instant.now()
    val user = User(idCounter.getAndIncrement(), email, name, role, now, now)
    users += user
    user
  }

  override def update(user: User): Future[User] = Future {
    val idx = users.indexWhere(_.id == user.id)
    val now = Instant.now()
    val updated = user.copy(updatedAt = now)
    users.update(idx, updated)
    updated
  }

  override def delete(id: Long): Future[Boolean] = Future {
    val idx = users.indexWhere(_.id == id)
    if (idx >= 0) {
      users.remove(idx)
      true
    } else false
  }
}
