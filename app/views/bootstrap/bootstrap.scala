package views.bootstrap

import views.html.bootstrap.input
import views.html.helper.{FieldConstructor, FieldElements}

object Implicits {
  implicit val fieldConstructor = new FieldConstructor {
    def apply(elements: FieldElements) =
      input(elements)
  }
}

object Helpers {
  def idToType(id: String): String = id match {
    case x if (x == "email") || (x == "password") => x
    case _ => "text"
  }
}
