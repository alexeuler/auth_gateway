package models

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.common.io.BaseEncoding
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import services.Encryption

case class User(id: Long = 0L,
                createdAt: Timestamp = new Timestamp(0L),
                updatedAt: Timestamp = new Timestamp(0L),
                email: String,
                password: String)

object User {
  def apply(email: String, password: String): User = User(0L, new Timestamp(0L), new Timestamp(0L), email, Encryption.md5(password))
  def tupled = (User.apply: (Long, Timestamp, Timestamp, String, String) => User).tupled
}


@Singleton
class UserRepo @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[User](dbConfigProvider) {
  import driver.api._

  class EntityTable(tag: Tag) extends BaseTable(tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id, createdAt, updatedAt, email, password) <> (User.tupled, User.unapply)
  }

  override def query = TableQuery[EntityTable]

}
