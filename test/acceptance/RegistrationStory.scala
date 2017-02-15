package acceptance

import helpers.{DatabaseCleaner, DefaultSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite}

class RegistrationStory extends DefaultSpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with DatabaseCleaner {
  it("Allows user to register") {
    go to s"http://localhost:$port/signup"

    emailField("email").value = "johndoe@gmail.com"
    pwdField("password").value = "password"
    submit()
    var text = ""
    eventually {
      text = find(tagName("body")).get.text
    }
    text should include ("Only users with confirmed email can see this page")
  }
}
