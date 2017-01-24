package controllers.auth

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(userRepo: UserRepo, val messagesApi: MessagesApi)(implicit exec: ExecutionContext)
  extends Controller with I18nSupport {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => {
        Future {
          Ok(views.html.auth.sessions.make(formWithErrors))
        }
      },
      userData => {
        Future { Ok(views.html.auth.sessions.make(userForm)) }
      }
    )
  }

  def make = Action { implicit request =>
    Ok(views.html.auth.sessions.make(userForm.discardingErrors))
  }
}
