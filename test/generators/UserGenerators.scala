package generators

import java.sql.Timestamp

import generators.fixtures.Fixtures
import models.Role.Role
import models.{Provider, Role, User}
import org.scalacheck.{Arbitrary, Gen}

object UserGenerators {
  def user(role: Role = Role.User): Gen[User] = for {
    email <- BasicGenerators.emailGen
  } yield new User(
    0L,
    new Timestamp(0L),
    new Timestamp(0L),
    Provider.Email,
    email,
    "password",
    role
  )

  def usersList(role: Role = Role.User): Gen[List[User]] = Gen.listOf(user(role)).map(_.distinct)

  implicit val arbUser = Arbitrary(user())
  implicit val arbUsersList = Arbitrary(usersList())
}
