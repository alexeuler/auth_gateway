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

  def create(user: User): Future[Long] = db.run(usersCreate(user))
  def all: Future[Seq[User]] = db.run(users.result)
  def find(id: Long): Future[Option[User]] = db.run(userWithId(id).result.headOption)

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")
    override def * = (id.?, email, password, createdAt.?, updatedAt.?) <> (User.tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]
  private val userWithId = (id: Long) => for (user <- users; if user.id === id) yield { user }
  private val usersCreate = (user: User) => {
    val now = Some(new Timestamp(new Date().getTime))
    val userWithTimestamp = user.copy(createdAt = now, updatedAt = now)
    (users returning users.map(_.id)) += userWithTimestamp
  }
}
