package generators

import java.sql.Timestamp

import generators.fixtures.Fixtures
import models.Role.Role
import models.{Provider, Role, User}
import org.scalacheck.{Arbitrary, Gen}

object UserGenerators {
  def userGen(role: Role = Role.User): Gen[User] = for {
    email <- BasicGenerators.emailGen
  } yield User(
    Provider.Email,
    email,
    "password",
    role
  )

  def usersList(role: Role = Role.User): Gen[List[User]] = Gen.listOf(userGen(role)).map(_.distinct)

  implicit val arbUser = Arbitrary(userGen())
  implicit val arbUsersList = Arbitrary(usersList())
}
