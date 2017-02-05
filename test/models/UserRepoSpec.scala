package models

import cats.implicits._
import com.mohiva.play.silhouette.api.LoginInfo
import helpers.{DefaultPropertyChecks, DefaultSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.OneAppPerSuite
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRepoSpec extends DefaultSpec with DefaultPropertyChecks with OneAppPerSuite {
  val userRepo = app.injector.instanceOf(classOf[UserRepo])
  val database = app.injector.instanceOf(classOf[DBApi]).database("default")

  before {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

  after {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

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
      describe("user with search params is in the db and unique") {
        it("returns Some(user)") {
          forAllAsync { users: List[User] =>
            for {
              dbUsers <- userRepo.create(users)
              foundUsers <- Future.traverse(dbUsers)(user =>
                userRepo.find(new LoginInfo(user.provider.toString, user.email))
              )
            } yield {
              (foundUsers.toList.sequence: Option[List[User]]) shouldBe Some(dbUsers)
            }
          }
        }
      }

      describe("No user with search params in the db") {
        it("Returns None") {
          forAllAsync { (user: User) =>
            for {
              dbUser <- userRepo.find(new LoginInfo(user.provider.toString, user.email))
            } yield {
              dbUser shouldBe None
            }
          }
        }
      }

      describe("Two users with the same credentials are in the db") {
        it("Returns failed future with TooManyFoundException") {
          forAll { (user: User) =>
            val future = for {
              _ <- userRepo.create(user)
              _ <- userRepo.create(user)
              _ <- userRepo.find(new LoginInfo(user.provider.toString, user.email))
            } yield ()
            ScalaFutures.whenReady(future.failed) { e =>
              e shouldBe a [ModelsExceptions.TooManyFoundException[_]]
            }
          }
        }
      }
    }
  }

}
