package controllers

import controllers.auth.{Registrations, Sessions}
import helpers.{DatabaseCleaner, DefaultSpec, fakeRequestWithSignedInUser, resultWithAuthenticator}
import models.UserRepo
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject._
import services.{Mail, MailMock}

class RegistrationsControllerSpec extends DefaultSpec with Results with DatabaseCleaner with GuiceOneAppPerSuite {
  lazy val mailer: MailMock = new MailMock
  override implicit lazy val app = new GuiceApplicationBuilder()
      .overrides(bind[Mail].toInstance(mailer))
      .build()
  val controller = app.injector.instanceOf[Registrations]
  val messagesApi = app.injector.instanceOf[MessagesApi]
  val userRepo = app.injector.instanceOf[UserRepo]
  implicit val messages = new Messages(Lang("en"), messagesApi)

  describe("make") {
    describe("user is not signed it") {
      it("renders registration form") {
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
        flash(result).data shouldBe Map("info" -> messagesApi("auth.already_signed_in"))
      }
    }
  }

  describe("create") {
    describe("correct credentials and user didn't exist") {
      it("creates and signs in an unconfirmed user and sends email") {
        val user = generators.UserGenerators.userGen().sample.get
        val request = FakeRequest("POST", controllers.auth.routes.Registrations.create().url)
          .withFormUrlEncodedBody("email" -> user.email, "password" -> "password")


        val result = route(app, request).get

        status(result) shouldBe 303
        redirectLocation(result).get shouldBe routes.Application.index().url
        flash(result).data shouldBe Map("success" -> messagesApi("auth.register_success", user.email))
        resultWithAuthenticator(result) shouldBe true
        mailer.last.get should include(user.email)
        mailer.last.get should include(messagesApi("mailer.auth.confirm.subject"))
      }
    }
  }
}