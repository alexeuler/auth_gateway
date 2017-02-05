package models

import com.mohiva.play.silhouette.api.LoginInfo
import helpers.{AsyncDefaultSpec, DefaultPropertyChecks, DefaultSpec}
import org.scalatestplus.play.OneAppPerSuite

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.implicits._
import play.api.db.Database
import play.db.evolutions.Evolutions

import scala.collection.mutable

class UserRepoSpec extends DefaultSpec with DefaultPropertyChecks with OneAppPerSuite {
  val userRepo = app.injector.instanceOf(classOf[UserRepo])

  describe("Stack") {
    it("pops values LIFO") {
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      stack.pop() should ===(2)
      stack.pop() should ===(1)
    }
  }

  describe("UserRepo") {
    import generators.UserGenerators._
    describe("find") {
      it("finds a user by login info (provider + id)") {
        forAllAsync { users: List[User] =>
          for {
            dbUsers <- userRepo.create(users)
            foundUsers <- Future.traverse(dbUsers)(user =>
              userRepo.find(new LoginInfo(user.provider.toString, user.email))
            )
            _ <- userRepo.clean
          } yield {
            (foundUsers.toList.sequence: Option[List[User]]) shouldBe Some(dbUsers)
          }
        }
      }
    }
  }

}
