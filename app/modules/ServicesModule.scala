package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services._

class ServicesModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Mail].to[MailImpl]
    bind[TokenService].to[TokenServiceImpl]
    bind[UserService].to[UserServiceImpl]
  }
}
