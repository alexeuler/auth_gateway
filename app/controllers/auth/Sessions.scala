package controllers.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import models.{User, UserRepo}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(
                          userRepo: UserRepo,
                          val messagesApi: MessagesApi,
                          silhouette: Silhouette[DefaultEnv]
                        )(
                          implicit exec: ExecutionContext
                        ) extends Controller with I18nSupport {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(views.html.auth.sessions.make(formWithErrors))),
      userForm => {
        val loginInfo = new LoginInfo("email", userForm.email)
        userRepo.find(loginInfo).flatMap {
          case None => Future.successful {
            Redirect(routes.Sessions.make())
              .flashing("danger" -> messagesApi("error.invalid_credentials"))
          }
          case Some(user) if user.password != userForm.password => Future.successful {
            Redirect(routes.Sessions.make())
              .flashing("danger" -> messagesApi("error.invalid_credentials"))
          }
          case Some(user) => for {
            authenticator <- silhouette.env.authenticatorService.create(loginInfo)
            session <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(
              session,
              Redirect(controllers.routes.Application.index())
                .flashing("success" -> messagesApi("auth.register_success", user.email))
            )
          } yield result
        }
      }
    )
  }

  def make = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case None => Ok(views.html.auth.sessions.make(userForm.discardingErrors))
      case Some(_) => Redirect(controllers.routes.Application.index())
          .flashing("info" -> Messages("auth.already_signed_in"))
    }

  }
}
