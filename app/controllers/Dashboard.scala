package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class Dashboard @Inject() (val silhouette: Silhouette[DefaultEnv],
                           val messagesApi: MessagesApi)
                          (implicit exec: ExecutionContext) extends Controller with I18nSupport {

  def index = Action.async { implicit request =>
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Ok(user.email)
      case HandlerResult(r, None) => Unauthorized
    }
  }

}
