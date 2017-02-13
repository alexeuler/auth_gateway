package mailers

import com.google.inject.Inject
import play.api.i18n.Messages
import play.api.mvc.RequestHeader
import services.Mail

import scala.concurrent.Future

trait AuthMailer {
  def confirmEmail(email: String, token: String)(implicit request: RequestHeader, messages: Messages): Future[String]
}

class AuthMailerImpl @Inject() (mail: Mail) extends AuthMailer {
  override def confirmEmail(email: String, token: String)(implicit request: RequestHeader, messages: Messages): Future[String] =
    mail.send(
      email,
      messages("mailer.auth.confirm.subject"),
      messages("mailer.auth.confirm.body", controllers.routes.Tokens.action(token).absoluteURL)
    )
}
