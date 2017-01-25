package controllers.auth

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.Future

class Registrations @Inject()(userRepo: UserRepo, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = Action { implicit request =>
    userForm.bindFromRequest().fold(
      formWithErrors => {
        Ok(views.html.auth.registrations.make(formWithErrors))
      },
      user => {
        Ok("Register here")
      }
    )
  }

  def make = Action { implicit request =>
    Ok(views.html.auth.registrations.make(userForm.discardingErrors))
  }
}