package controllers

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Lang, Messages, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(userRepo: UserRepo, val messagesApi: MessagesApi)(implicit exec: ExecutionContext) extends Controller with I18nSupport {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => {
        Future {
          Ok(views.html.sessions.make(formWithErrors))
        }
      },
      userData => {
        Future { Ok(views.html.sessions.make(userForm)) }
      }
    )
  }

  def make = Action { implicit request =>
    Ok(views.html.sessions.make(userForm.discardingErrors))
  }
}
