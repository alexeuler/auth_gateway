package controllers

import controllers.auth.Sessions
import helpers.{DefaultSpec, _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._


class SessionsControllerSpec extends DefaultSpec with Results with GuiceOneAppPerSuite {
  val controller = app.injector.instanceOf[Sessions]
  val messagesApi = app.injector.instanceOf[MessagesApi]
  implicit val messages = new Messages(Lang("en"), messagesApi)

  describe("make") {
    describe("user is not signed it") {
      it("renders loginForm") {
        val result = controller.make().apply(FakeRequest())
        status(result) shouldBe 200
        val page = contentAsString(result)
        page should include(Messages("auth.email"))
        page should include(Messages("auth.password"))
      }
    }

    describe("user is signed in") {
      it("redirects to root with flash message") {
        val request = fakeRequestWithSignedInUser
        val result = controller.make(request)
        status(result) shouldBe 303
        redirectLocation(result).get shouldBe "/"
        flash(result).data shouldBe Map("info" -> Messages("auth.already_signed_in"))
      }
    }
  }
}
