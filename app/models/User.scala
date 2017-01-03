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
                 override val id: Option[Long],
                 override val createdAt: Option[Timestamp],
                 override val updatedAt: Option[Timestamp],
                 email: String,
                 password: String) extends Model[User] {

  override def setTimeStamps(
                              createdAt: Option[Timestamp],
                              updatedAt: Option[Timestamp]
                            ): User = this.copy(createdAt = createdAt, updatedAt = updatedAt)

  def this(email: String, password: String) = this(None, None, None, email, User.md5(password))
  def hasPassword(pass: String): Boolean = User.md5(pass) == password

}

class UserRepo @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Models[User](dbConfigProvider = dbConfigProvider) {
  import driver.api._

  class UserTable(tag: Tag) extends ModelTable(tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id.?, createdAt.?, updatedAt.?, email, password) <> ((User.apply _).tupled, User.unapply)
  }

  object queries extends Queries[UserTable] {
    def findByCredentials(email: String, password: String): Future[Option[User]] =
      db.run(filterByEmailQuery(email).result.map(_.find(_.hasPassword(password))))

    override def allQuery: TableQuery[UserTable] = TableQuery[UserTable]
    private val filterByEmailQuery = (email: String) =>
      for (user <- allQuery; if user.email === email) yield user
  }
}
