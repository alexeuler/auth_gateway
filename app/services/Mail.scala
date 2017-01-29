package services

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.mailer.{Email, MailerClient}
import net.ceedubs.ficus.Ficus._
import scala.concurrent.{ExecutionContext, Future}

trait Mail {
  def send(email: String, subject: String, body: String): Future[String]
}

class MailImpl @Inject() (mailerClient: MailerClient, config: Configuration)(implicit exec: ExecutionContext)
  extends Mail {

  lazy val from: String = config.underlying.as[String]("play.mailer.from")

  override def send(email: String, subject: String, body: String): Future[String] = Future {
    mailerClient.send(Email(subject, from, Seq(email), None, Some(body)))
  }
}
