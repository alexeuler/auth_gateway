package generators

import java.sql.Timestamp

import generators.fixtures.Fixtures
import models.Role.Role
import models.{Provider, Role, User}
import org.scalacheck.{Arbitrary, Gen}

object UserGenerators {
  def user(role: Role = Role.User): Gen[User] = for {
    email <- Basic.emailGen
  } yield new User(
    0L,
    new Timestamp(0L),
    new Timestamp(0L),
    Provider.Email,
    email,
    "password",
    role
  )

  def usersList(role: Role = Role.User): Gen[List[User]] = Gen.listOf(user(role))
  def usersSet(role: Role = Role.User): Gen[Set[User]] = usersList(role).map(_.toSet)

  implicit val arbUser = Arbitrary(user())
  implicit val arbUsersList = Arbitrary(usersList())
  implicit val arbUsersSet = Arbitrary(usersSet())
}
