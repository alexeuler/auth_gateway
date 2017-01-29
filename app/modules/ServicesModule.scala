package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.{Mail, MailImpl}

class ServicesModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Mail].to[MailImpl]
  }
}
