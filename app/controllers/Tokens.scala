package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.{TokenRepo, UserRepo}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}
import services.TokenService
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class Tokens @Inject()(
                        tokenService: TokenService,
                        val messagesApi: MessagesApi,
                        silhouette: Silhouette[DefaultEnv]
                      )(
                        implicit exec: ExecutionContext
                      ) extends Controller with I18nSupport {

  def action(tokenValue: String) = silhouette.UserAwareAction.async { implicit request =>
    for (result <- tokenService.handle(tokenValue)) yield result match {
      case true => Redirect(routes.Application.index())
        .flashing("success" -> messagesApi("auth.confirm_success"))
      case _ => Redirect(controllers.auth.routes.Registrations.make())
        .flashing("danger" -> messagesApi("error.invalid_confirmation_code"))
    }
  }
}
