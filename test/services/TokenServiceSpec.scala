package services

import generators.TokenGenerators.{tokenGen, tokenWithUnconfirmedUser}
import generators.UserGenerators.userGen
import helpers.{DatabaseCleaner, DefaultPropertyChecks, DefaultSpec}
import models._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class TokenServiceSpec extends DefaultSpec with DefaultPropertyChecks with DatabaseCleaner with GuiceOneAppPerSuite {
  val userRepo = app.injector.instanceOf(classOf[UserRepo])
  val tokenRepo = app.injector.instanceOf(classOf[TokenRepo])
  val tokenService = app.injector.instanceOf(classOf[TokenService])


  describe("handle") {
    describe("token with specified value exists, user specified by token exists") {
      it("gives user privileges specified by token, deletes that token and returns true") {
        forAllAsync(tokenWithUnconfirmedUser) { case (token: Token, user: User) =>
          for {
            _ <- Future.sequence(List(userRepo.create(user), tokenRepo.create(token)))
            result <- tokenService.handle(token.value)
            dbToken <- tokenRepo.find(token.value)
            dbUser <- userRepo.find(user.toLoginInfo)
          } yield {
            dbToken.isEmpty shouldBe true
            dbUser.isEmpty shouldBe false
            dbUser.get.role shouldBe Role.User
            result shouldBe true
          }
        }
      }
    }

    describe("token doesn't exist") {
      it("affects nothing and returns false") {
        forAllAsync(tokenWithUnconfirmedUser) { case (token: Token, user: User) =>
          for {
            _ <- userRepo.create(user)
            result <- tokenService.handle(token.value)
            dbToken <- tokenRepo.find(token.value)
            dbUser <- userRepo.find(user.toLoginInfo)
          } yield {
            dbToken.isEmpty shouldBe true
            dbUser.isEmpty shouldBe false
            dbUser.get.role shouldBe Role.Unconfirmed
            result shouldBe false
          }
        }
      }
    }

    describe("user doesn't exist") {
      it("affects nothing and returns false") {
        forAllAsync(tokenWithUnconfirmedUser) { case (token: Token, user: User) =>
          for {
            _ <- tokenRepo.create(token)
            result <- tokenService.handle(token.value)
            dbToken <- tokenRepo.find(token.value)
            dbUser <- userRepo.find(user.toLoginInfo)
          } yield {
            dbToken.isEmpty shouldBe false
            dbUser.isEmpty shouldBe true
            result shouldBe false
          }
        }
      }
    }

    describe("user and token are not related") {
      it("affects nothing and returns false") {
        forAllAsync(tokenGen(), userGen(Role.Unconfirmed)) { (token: Token, user: User) =>
          for {
            _ <- Future.sequence(List(userRepo.create(user), tokenRepo.create(token)))
            result <- tokenService.handle(token.value)
            dbToken <- tokenRepo.find(token.value)
            dbUser <- userRepo.find(user.toLoginInfo)
          } yield {
            dbToken.isEmpty shouldBe false
            dbUser.isEmpty shouldBe false
            dbUser.get.role shouldBe Role.Unconfirmed
            result shouldBe false
          }
        }
      }
    }

  }
}
