package controllers.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.mohiva.play.silhouette.api.services.IdentityService
import mailers.AuthMailer
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.Mail
import silhouette.{DefaultEnv, UserService}

import scala.concurrent.{ExecutionContext, Future}

class Registrations @Inject()(
                               userRepo: UserRepo,
                               userRegisterTokenRepo: TokenRepo,
                               val messagesApi: MessagesApi,
                               silhouette: Silhouette[DefaultEnv],
                               userService: IdentityService[User],
                               authMailer: AuthMailer
                             )(
                               implicit ex: ExecutionContext
                             ) extends Controller with I18nSupport {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = silhouette.UnsecuredAction.async { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => Future.successful { BadRequest(views.html.auth.registrations.make(formWithErrors)) },
      user => {
        val loginInfo: LoginInfo = LoginInfo("email", user.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(_) => Future.successful { BadRequest(views.html.auth.registrations.make(userForm.withError("email", "error.email_not_unique"))) }
          case None => {
            val token = Token(payload = loginInfo.providerKey, action = TokenAction.Register)
            val tokenResult = userRegisterTokenRepo.create(token)
            val userResult = userRepo.create(user)
            val mailerResult = authMailer.confirmEmail(user.email, token.value)
            for {
              savedToken <- tokenResult
              savedUser <- userResult
              _ <- mailerResult
            } yield {
              Redirect(routes.Sessions.make())
                .flashing("success" -> messagesApi("auth.register_success", user.email))
            }
          }
        }
      }
    )
  }

  def make = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case Some(_) => Redirect(controllers.routes.Application.index())
      case None => Ok(views.html.auth.registrations.make(userForm.discardingErrors))
    }
  }

  def confirm(token: String) = silhouette.UnsecuredAction {
    Ok(s"Confirmed $token")
  }
}
