package models

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.common.io.BaseEncoding
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.db.slick.DatabaseConfigProvider
import services.Encryption

import scala.concurrent.Future

object Provider extends Enumeration {
  type Provider = Value
  val Email = Value("email")
}

object Role extends Enumeration {
  type Role = Value
  val Unconfirmed = Value("unconfirmed")
  val User = Value("user")
  val Admin = Value("admin")
}

import Provider._
import Role._

case class User(id: Long = 0L,
                createdAt: Timestamp = new Timestamp(0L),
                updatedAt: Timestamp = new Timestamp(0L),
                provider: Provider,
                email: String,
                password: String,
                role: Role
               ) extends Identity {

  def hasPassword(pass: String): Boolean = Encryption.md5(pass) == password
}

object User {
  def apply(provider: Provider, email: String, password: String): User =
    User(
      0L,
      new Timestamp(0L),
      new Timestamp(0L),
      provider,
      email.toLowerCase,
      Encryption.md5(password),
      if (provider == Email) Role.Unconfirmed else Role.User
    )

  def apply(email: String, password: String): User = apply(Email, email, password)

  def tupled = (User.apply: (Long, Timestamp, Timestamp, Provider, String, String, Role) => User).tupled
}