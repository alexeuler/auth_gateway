package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.{TokenRepo, UserRepo}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class Tokens @Inject()(
                        userRepo: UserRepo,
                        tokenRepo: TokenRepo,
                        val messagesApi: MessagesApi,
                        silhouette: Silhouette[DefaultEnv]
                      )(
                        implicit exec: ExecutionContext
                      ) extends Controller with I18nSupport {

  def action(tokenValue: String) = silhouette.UnsecuredAction.async { implicit request =>
    tokenRepo.find(tokenValue).flatMap {
      case None => Future.successful {
        Redirect(controllers.auth.routes.Registrations.make())
          .flashing("danger" -> messagesApi("error.invalid_confirmation_code"))
      }
      case Some(token) => {
        for (success <- token.handle(userRepo, tokenRepo)) yield {
          if (success) {
            Redirect(controllers.auth.routes.Sessions.make())
              .flashing("success" -> messagesApi("auth.confirm_success"))
          } else {
            Redirect(controllers.auth.routes.Registrations.make())
              .flashing("danger" -> messagesApi("error.invalid_confirmation_code"))
          }
        }
      }
    }
  }

}
