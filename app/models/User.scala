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
               ) extends Identity

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

trait UserRepo {
  def find(loginInfo: LoginInfo): Future[Option[User]]
  def create(user: User): Future[User]
}

@Singleton
class UserRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[User](dbConfigProvider) with UserRepo {
  import driver.api._

  implicit val roleMapper = MappedColumnType.base[Role, String](
    action => action.toString,
    string => Role.withName(string)
  )

  implicit val providerMapper = MappedColumnType.base[Provider, String](
    action => action.toString,
    string => Provider.withName(string)
  )

  class EntityTable(tag: Tag) extends BaseTable(tag, "users") {
    def provider = column[Provider]("provider")
    def email = column[String]("email")
    def password = column[String]("password")
    def role = column[Role]("role")
    override def * = (id, createdAt, updatedAt, provider, email, password, role) <> (User.tupled, User.unapply)
  }

  object UserQueries {
    def filter(loginInfo: LoginInfo): Query[EntityTable, User, Seq] =
      for (user <- query; if (user.email === loginInfo.providerKey) && (user.provider === Provider.withName(loginInfo.providerID))) yield user
  }

  object UserActions {
    def find(loginInfo: LoginInfo): DBIO[Option[User]] = UserQueries.filter(loginInfo).result.headOption
  }

  override def query = TableQuery[EntityTable]

  override def find(loginInfo: LoginInfo): Future[Option[User]] = db.run(UserActions.find(loginInfo))
}
