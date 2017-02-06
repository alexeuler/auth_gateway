package models

import java.sql.Statement

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

  // Use this to insert duplicate users for tests (User repo API prohibits this)
  def createUserWithSql(user: User): Unit = {
    database.withConnection { connection =>
      val statement = connection.createStatement()
      val sql = s"""INSERT INTO users (provider, email, password, role)
                   |VALUES ('${user.provider}', '${user.email}', '${user.password}', '${user.role}')
                 """.stripMargin
      statement.execute(sql)
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

      describe("More than 1 user with such credentials in the db") {
        it("Throws a TooManyFound exception") {
          forAll { user: User =>
            createUserWithSql(user)
            createUserWithSql(user)
            val future = for {
              _ <- userRepo.find(user.toLoginInfo)
            } yield ()
            ScalaFutures.whenReady(future.failed) { e =>
              e shouldBe a[ModelsExceptions.TooManyFoundException[_]]
            }
          }
        }
      }
    }

    describe("create") {
      describe("single user") {
        it("stores user in the db with updated id, createdAt and updatedAt timestamps") {
          forAllAsync { user: User =>
            for {
              dbUser <- userRepo.create(user)
            } yield {
              dbUser.id should not be user.id
              dbUser.createdAt should not be user.createdAt
              dbUser.updatedAt should not be user.updatedAt
              dbUser.email shouldBe user.email
            }
          }
        }

        describe("entity already exists") {
          it("throws an AlreadyExists exception") {
            forAll { (user: User) =>
              val future = for {
                _ <- userRepo.create(user)
                _ <- userRepo.create(user)
              } yield ()
              ScalaFutures.whenReady(future.failed) { e =>
                e shouldBe a[ModelsExceptions.AlreadyExists[_]]
              }
            }
          }
        }
      }

      describe("many users") {
        it("stores user in the db with updated id, createdAt and updatedAt timestamps") {
          forAllAsync { users: List[User] =>
            userRepo.create(users).map(dbUsers =>
              dbUsers.map(dbUser => {
                // default id and timestamps are the same for all generated users => can take first
                val user = users.head
                dbUser.id should not be user.id
                dbUser.createdAt should not be user.createdAt
                dbUser.updatedAt should not be user.updatedAt
                users.map(_.email) should contain(dbUser.email)
              })
            )
          }
        }

        describe("arguments contain users with the same login info") {
          it("throws an IllegalArgument exception") {
            forAll { (user: User) =>
              val future = for {
                _ <- userRepo.create(List(user, user))
              } yield ()
              ScalaFutures.whenReady(future.failed) { e =>
                e shouldBe a[IllegalArgumentException]
              }
            }
          }
        }

        describe("user with specified credentials already exists") {
          it("throws an AlreadyExists exception") {
            forAll { (user: User) =>
              val future = for {
                _ <- userRepo.create(user)
                _ <- userRepo.create(List(user))
              } yield ()
              ScalaFutures.whenReady(future.failed) { e =>
                e shouldBe a[ModelsExceptions.AlreadyExists[_]]
              }
            }
          }
        }
      }
    }

    describe("update Role") {
      describe("user exists") {
        it("updates a users role and returns true") {
          forAllAsync { user: User =>
            for {
              _ <- userRepo.create(user)
              result <- userRepo.updateRole(user.toLoginInfo, Role.Admin)
              fetchedUser <- userRepo.find(user.toLoginInfo)
            } yield {
              result shouldBe true
              fetchedUser.get.role shouldBe Role.Admin
            }
          }
        }
      }

      describe("user doesn't exist") {
        it("doesn't update and returns false") {
          forAllAsync { user: User =>
            for {
              result <- userRepo.updateRole(user.toLoginInfo, Role.Admin)
            } yield {
              result shouldBe false
            }
          }
        }
      }

      describe("more that 1 user exists") {
        it("Throws a TooManyFound exception") {
          forAll { user: User =>
            createUserWithSql(user)
            createUserWithSql(user)
            val future = for {
              _ <- userRepo.updateRole(user.toLoginInfo, Role.Admin)
            } yield ()
            ScalaFutures.whenReady(future.failed) { e =>
              e shouldBe a[ModelsExceptions.TooManyFoundException[_]]
            }
          }
        }
      }
    }
  }
}
