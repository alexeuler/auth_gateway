package controllers

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class Sessions @Inject()(userRepo: UserRepo)(implicit exec: ExecutionContext) extends Controller {

//  val userForm: Form[User] = Form(
//    mapping(
//      "email" -> nonEmptyText,
//      "password" -> nonEmptyText
//    )(
//      (email: String, password: String) => new User(email, password)
//    )(user => Some(user.email, ""))
//  )
//
//  def make = Action.async { implicit request =>
//    userForm.bindFromRequest().fold(
//      errors => {
//        Future { Ok(views.html.sessions.make()) }
//      },
//      userData => {
//        for {
//          user <- userRepo.queries.findByCredentials(userData.email, userData.password)
//        } yield user match {
//            case None => Ok(views.html.sessions.make())
//            case _ => Ok(views.html.sessions.make())
//        }
//      }
//    )
//  }

  def make = Action.async {
    for (user <- userRepo.queries.create(User(email = "123@gmail.com", password = "123"))) yield {
      Ok(views.html.sessions.make())
    }
//    for (users <- userRepo.queries.all) yield {
//      Ok(users.toString())
//    }
  }
}
