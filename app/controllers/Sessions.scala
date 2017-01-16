package controllers

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(userRepo: UserRepo)(implicit exec: ExecutionContext) extends Controller {

  val userForm: Form[User] = Form(
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(user => Some(user.email, ""))
  )

  def create = Action.async { implicit request =>
    userForm.bindFromRequest().fold(
      errors => {
        Future { Ok(views.html.sessions.make()) }
      },
      userData => {
        Future { Ok(views.html.sessions.make()) }
      }
    )
  }

  def make = Action {
    Ok(views.html.sessions.make())
  }
}
