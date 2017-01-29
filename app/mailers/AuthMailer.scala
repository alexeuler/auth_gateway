package mailers

import com.google.inject.Inject
import controllers.auth.routes
import play.api.mvc.RequestHeader
import services.Mail

import scala.concurrent.Future

trait AuthMailer {
  def confirmEmail(email: String, token: String)(implicit request: RequestHeader): Future[String]
}

class AuthMailerImpl @Inject() (mail: Mail) extends AuthMailer {
  override def confirmEmail(email: String, token: String)(implicit request: RequestHeader): Future[String] =
    mail.send(
      email,
      "Please confirm your email",
      "Please confirm your email by clicking this " +
      s"<a href=${controllers.auth.routes.Registrations.confirm(token).absoluteURL}>link</a>."
    )
}
