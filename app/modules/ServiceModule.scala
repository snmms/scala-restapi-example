// app/modules/ServiceModule.scala
package modules

import com.google.inject.AbstractModule
import services._
import repositories._

class ServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl])
    bind(classOf[PasswordService]).to(classOf[BCryptPasswordService])
    bind(classOf[AuthService]).to(classOf[AuthServiceImpl])
  }
}
