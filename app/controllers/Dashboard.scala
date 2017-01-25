package controllers

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

class Dashboard @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok("Ok")
  }

}
