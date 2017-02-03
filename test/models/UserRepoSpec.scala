package models

import com.mohiva.play.silhouette.api.LoginInfo
import helpers.{DefaultPropertyChecks, DefaultSpec}
import org.scalatestplus.play.OneAppPerSuite

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.implicits._

import scala.collection.mutable

class UserRepoSpec extends DefaultSpec with DefaultPropertyChecks with OneAppPerSuite {
  val userRepo = app.injector.instanceOf(classOf[UserRepo])

  describe("Stack") {
    it("pops values LIFO") {
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      stack.pop() should === (2)
      stack.pop() should === (1)
    }
  }

  describe("UserRepo") {
    import generators.UserGenerators._
    describe("find") {
      it("finds a user by login info (provider + id)") {
        forAll { (users: List[User]) =>
          val res = for {
            dbUsers <- userRepo.create(users)
            foundMaybeUsers <- Future.traverse(dbUsers)(user =>
              userRepo.find(new LoginInfo(user.provider.toString, user.email))
            )
          } yield {
            val maybeFoundUsers: Option[List[User]] = foundMaybeUsers.toList.sequence
            println(maybeFoundUsers)
            maybeFoundUsers shouldBe Some(dbUsers)
          }
//          val res: Future[List[String]] = userRepo.create(users).flatMap(_ => Future.sequence(
//            users.map(user => userRepo.find(new LoginInfo(user.provider.toString, user.email))
//            )).map(list => list.map(_.get.email))).map(x => { println(x); x shouldBe users.map(_.email); x})

          Await.result(res, 5000 millis)
          val b = 1
        }
      }
    }
  }

}
