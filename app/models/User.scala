package models

import java.security.MessageDigest
import java.sql.Timestamp

import com.google.common.io.BaseEncoding
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.db.slick.DatabaseConfigProvider
import services.Encryption

import scala.concurrent.Future

case class User(id: Long = 0L,
                createdAt: Timestamp = new Timestamp(0L),
                updatedAt: Timestamp = new Timestamp(0L),
                provider: String,
                email: String,
                password: String) extends Identity

object User {
  def apply(provider: String, email: String, password: String): User =
    User(0L, new Timestamp(0L), new Timestamp(0L), provider, email.toLowerCase, Encryption.md5(password))

  def apply(email: String, password: String): User = apply("email", email, password)

  def tupled = (User.apply: (Long, Timestamp, Timestamp, String, String, String) => User).tupled
}

trait UserRepo {
  def find(loginInfo: LoginInfo): Future[Option[User]]
}

@Singleton
class UserRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[User](dbConfigProvider) with UserRepo {
  import driver.api._

  class EntityTable(tag: Tag) extends BaseTable(tag, "users") {
    def provider = column[String]("provider")
    def email = column[String]("email")
    def password = column[String]("password")
    override def * = (id, createdAt, updatedAt, provider, email, password) <> (User.tupled, User.unapply)
  }

  object UserQueries {
    def filter(loginInfo: LoginInfo): Query[EntityTable, User, Seq] =
      for (user <- query; if (user.email === loginInfo.providerKey) && (user.provider === loginInfo.providerID)) yield user
  }

  object UserActions {
    def find(loginInfo: LoginInfo): DBIO[Option[User]] = UserQueries.filter(loginInfo).result.headOption
  }

  override def query = TableQuery[EntityTable]

  override def find(loginInfo: LoginInfo): Future[Option[User]] = db.run(UserActions.find(loginInfo))
}
