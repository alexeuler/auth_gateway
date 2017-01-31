package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import models.Role
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class Dashboard @Inject() (val silhouette: Silhouette[DefaultEnv],
                           val messagesApi: MessagesApi)
                          (implicit exec: ExecutionContext) extends Controller with I18nSupport {

  def index = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case None => Redirect(auth.routes.Sessions.make())
        .flashing("danger" -> messagesApi("dashboard.require_login"))
      case Some(user) => user.role match {
        case Role.Unconfirmed => Ok(views.html.dashboard.not_confirmed())
        case Role.User => Ok(views.html.dashboard.index())
      }
    }
  }

}
