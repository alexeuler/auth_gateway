package models

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.common.io.BaseEncoding
import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

object User {
  private def md5(s: String): String = {
    val bytes = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))
    BaseEncoding.base64().encode(bytes)
  }
}


case class User(
                 override val id: Long = 0L,
                 override val createdAt: Timestamp = new Timestamp(0L),
                 override val updatedAt: Timestamp = new Timestamp(0L),
                 email: String,
                 password: String) extends Model

class UserRepo @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Models[User](dbConfigProvider = dbConfigProvider) {
  import driver.api._

  class UserTable(tag: Tag) extends ModelTable(tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id, createdAt, updatedAt, email, password) <> ((User.apply _).tupled, User.unapply)
  }

  object queries extends Queries[UserTable] {
    override def modelsQuery: TableQuery[UserTable] = TableQuery[UserTable]
    private val filterByEmailQuery = (email: String) =>
      for (user <- modelsQuery; if user.email === email) yield user
  }
}
