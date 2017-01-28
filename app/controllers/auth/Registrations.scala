package controllers.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import com.mohiva.play.silhouette.api.services.IdentityService
import models.{User, UserRepo}
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import silhouette.{DefaultEnv, UserService}

import scala.concurrent.{ExecutionContext, Future}

class Registrations @Inject()(
                               userRepo: UserRepo,
                               val messagesApi: MessagesApi,
                               silhouette: Silhouette[DefaultEnv],
                               userService: IdentityService[User]
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
      formWithErrors => Future { BadRequest(views.html.auth.registrations.make(formWithErrors)) },
      user => {
        val loginInfo: LoginInfo = LoginInfo("email", user.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(_) => Future { BadRequest(views.html.auth.registrations.make(userForm.withError("email", "error.email_not_unique"))) }
          case None => Future { Ok("Ok") }
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
}
