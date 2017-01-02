package models

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

case class User(
                 id: Option[Long] = None,
                 email: String,
                 password: String,
                 createdAt: Option[Timestamp] = None,
                 updatedAt: Option[Timestamp] = None
               )

class Users @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private class UsersReadTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")
    override def * = (id.?, email, password, createdAt.?, updatedAt.?) <> (User.tupled, User.unapply)
  }

  private class UsersWriteTable(tag: Tag) extends Table[(String, String)](tag, "users") {
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (email, password)
  }

  def all: Future[Seq[User]] = db.run(usersReadQuery.result)
  def create(user: User): Future[(String, String)] = db.run(usersWriteQuery returning usersWriteQuery += (user.email, user.password))
  def find(id: Long): Future[Option[User]] = db.run(usersWithIdQuery(id).result.headOption)

  private val usersReadQuery = TableQuery[UsersReadTable]
  private val usersWriteQuery = TableQuery[UsersWriteTable]
  private val usersWithIdQuery = (id: Long) => for (user <- usersReadQuery; if user.id === id) yield { user }
}
