package controllers

import play.api._
import play.api.mvc._

class Sessions extends Controller {
  def make = Action {
    Ok(views.html.sessions.make())
  }
}
