package models

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.common.io.BaseEncoding
import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

object User {
  private def md5(s: String): String = {
    val bytes = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))
    BaseEncoding.base64().encode(bytes)
  }
}

case class User(id: Long = 0L,
                createdAt: Timestamp = new Timestamp(0L),
                updatedAt: Timestamp = new Timestamp(0L),
                email: String,
                password: String)

class UserRepo @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[User](dbConfigProvider) {
  import driver.api._

  class EntityTable(tag: Tag) extends BaseTable(tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id, createdAt, updatedAt, email, password) <> ((User.apply _).tupled, User.unapply)
  }

  override def query = TableQuery[EntityTable]

}
