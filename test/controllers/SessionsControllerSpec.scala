package controllers

import controllers.auth.Sessions
import generators.UserGenerators
import helpers.{DefaultSpec, _}
import models.UserRepo
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.duration._

import scala.concurrent.Await


class SessionsControllerSpec extends DefaultSpec with Results with DatabaseCleaner with GuiceOneAppPerSuite {
  val controller = app.injector.instanceOf[Sessions]
  val messagesApi = app.injector.instanceOf[MessagesApi]
  val userRepo = app.injector.instanceOf[UserRepo]
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
        redirectLocation(result).get shouldBe routes.Application.index().url
        flash(result).data shouldBe Map("info" -> Messages("auth.already_signed_in"))
      }
    }
  }

  describe("create") {
    describe("there's a confirmed user with specified credentials in DB") {
      it("signs in") {
        val user = UserGenerators.userGen().sample.get
        Await.result(userRepo.create(user), 1000 millis)
        val request = FakeRequest("POST", controllers.auth.routes.Sessions.create().url)
          .withFormUrlEncodedBody("email" -> user.email, "password" -> "password")
        val result = route(app, request).get
        status(result) shouldBe 303
        redirectLocation(result).get shouldBe routes.Application.index().url
        flash(result).data shouldBe Map("success" -> messagesApi("auth.register_success", user.email))
        resultWithAuthenticator(result) shouldBe true
      }
    }
  }
}
