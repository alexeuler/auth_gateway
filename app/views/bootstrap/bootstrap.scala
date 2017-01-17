package views.bootstrap

import views.html.bootstrap.input
import views.html.helper.{FieldConstructor, FieldElements}

object Implicits {
  implicit val fieldConstructor = new FieldConstructor {
    def apply(elements: FieldElements) =
      input(elements)
  }

//  val f = FieldConstructor(input.render)
}
