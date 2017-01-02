package controllers

import com.google.inject.Inject
import models.{User, Users}
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class Sessions @Inject()(usersRepo: Users)(implicit exec: ExecutionContext) extends Controller {
  def make = Action.async {
    for (user <- usersRepo.create(User(email = "123@gmail.com", password = "123"))) yield {
      Ok(views.html.sessions.make())
    }
//    for (users <- usersRepo.all; count = users.size) yield {
//      if (count == 0) {
//        for (user <- usersRepo.create(User(email = "123@gmail.com", password = "123"))) yield {
//          val a = 1
//        }
//      }
//      Ok(views.html.sessions.make())
//    }
  }
}
