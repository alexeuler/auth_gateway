package models

import java.sql.Timestamp

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider

case class User(
                 override val id: Option[Long] = None,
                 override val createdAt: Option[Timestamp] = None,
                 override val updatedAt: Option[Timestamp] = None,
                 email: String,
                 password: String) extends Model[User] {

  override def setTimeStamps(
                              createdAt: Option[Timestamp],
                              updatedAt: Option[Timestamp]
                            ): User = this.copy(createdAt = createdAt, updatedAt = updatedAt)
}

class Users @Inject()(override val dbConfigProvider: DatabaseConfigProvider) extends Models[User](dbConfigProvider = dbConfigProvider) {
  import driver.api._

  class UserTable(tag: Tag) extends ModelTable(tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id.?, createdAt.?, updatedAt.?, email, password) <> (User.tupled, User.unapply)
  }

  object queries extends Queries[UserTable] {
    override def allQuery = TableQuery[UserTable]
  }
}
