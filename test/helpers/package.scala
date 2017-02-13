import com.google.inject.Injector
import com.mohiva.play.silhouette.api.Silhouette
import models.{User, UserRepo}
import play.api.Application
import play.api.mvc.{AnyContentAsEmpty, Request, Result}

import scala.concurrent.duration._
import net.codingwell.scalaguice.InjectorExtensions._
import com.mohiva.play.silhouette.test._
import generators.UserGenerators.userGen
import play.api.test.FakeRequest
import silhouette.DefaultEnv

import scala.concurrent.{Await, Future}

package object helpers {
  def fakeRequestWithSignedInUser(user: User)(implicit app: Application): FakeRequest[AnyContentAsEmpty.type] = {
    // Extracting guice injector from Play, that plays nicely with scalaguice
    val injector = app.injector.instanceOf[Injector]
    val userRepo = injector.instance[UserRepo]
    Await.result(userRepo.create(user), 1000 millis)

    // This is where scalaguice relieves the pain. Nested generics are hard
    // to instantiate directly
    val silhouette = injector.instance[Silhouette[DefaultEnv]]
    implicit val env = silhouette.env
    FakeRequest().withAuthenticator(user.toLoginInfo)
  }

  def fakeRequestWithSignedInUser(implicit app: Application): FakeRequest[AnyContentAsEmpty.type] = {
    val user = userGen().sample.get
    fakeRequestWithSignedInUser(user)
  }

  def resultWithAuthenticator(result: Future[Result]): Boolean = {
    val session = Await.result(result, 1000 millis).header.headers("Set-Cookie")
    session.contains("authenticator=")
  }
}
