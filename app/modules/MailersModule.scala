package modules

import com.google.inject.AbstractModule
import mailers.{AuthMailer, AuthMailerImpl}
import net.codingwell.scalaguice.ScalaModule

class MailersModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[AuthMailer].to[AuthMailerImpl]
  }
}
