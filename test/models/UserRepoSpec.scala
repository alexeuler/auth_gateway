package models

import cats.implicits._
import com.mohiva.play.silhouette.api.LoginInfo
import helpers.{DatabaseCleaner, DefaultPropertyChecks, DefaultSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.OneAppPerSuite

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRepoSpec extends DefaultSpec with DefaultPropertyChecks with OneAppPerSuite with DatabaseCleaner {
  val userRepo = app.injector.instanceOf(classOf[UserRepo])

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
