package controllers

import com.google.inject.Inject
import models.{User, UserRepo}
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class Sessions @Inject()(userRepo: UserRepo)(implicit exec: ExecutionContext) extends Controller {
  def make = Action {
      Ok(views.html.sessions.make())
  }
}
