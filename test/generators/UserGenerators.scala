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

  def users(role: Role = Role.User): Gen[List[User]] = Gen.listOf(user(role))

//  def users(role: Role = Role.User): Gen[List[User]] = for {
//    num <- Basic.smallNumGen
//    users <- Gen.listOfN(num, user(role))
//  } yield users

  implicit val arbUser = Arbitrary(user())
  implicit val arbUsers = Arbitrary(users())
}
