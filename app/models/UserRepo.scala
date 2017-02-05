package models

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import models.ModelsExceptions.TooManyFoundException
import models.Provider.Provider
import models.Role.Role
import play.api.db.Database
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait UserRepo {
  def find(loginInfo: LoginInfo): Future[Option[User]]
  def create(user: User): Future[User]
  def create(users: Iterable[User]): Future[Seq[User]]
  def updateRole(loginInfo: LoginInfo, role: Role): Future[Boolean]
}

@Singleton
class UserRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext)
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
    def filterByEmails(emails: Iterable[String]): Query[EntityTable, User, Seq] =
      for (user <- query; if user.email inSet emails) yield user
  }

  object UserActions {
    def find(loginInfo: LoginInfo): DBIO[Option[User]] = UserQueries.filter(loginInfo).result.flatMap(users =>
      if (users.size < 2) DBIO.successful(users.headOption) else DBIO.failed(TooManyFoundException(loginInfo))
    )
    def create(user: User): DBIO[User] =
      find(new LoginInfo(user.provider.toString, user.email)).flatMap {
        case Some(_) => DBIO.failed(ModelsExceptions.AlreadyExists(user))
        case None => query returning query += user
      }
    def create(users: Iterable[User]): DBIO[Seq[User]] = {
      // If there are two users with the same credentials in arguments throw an exception
      if (users.map(_.toLoginInfo).toSet.size != users.size) return DBIO.failed(new IllegalArgumentException)
      UserQueries.filterByEmails(users.map(_.email)).result.flatMap(existingUsers => {
        val commonUsers = users.map(_.toLoginInfo).toSet.intersect(existingUsers.map(_.toLoginInfo).toSet)
        if (commonUsers.nonEmpty)
          DBIO.failed(ModelsExceptions.AlreadyExists(commonUsers))
        else
          query returning query ++= users
      })
    }
    def updateRole(loginInfo: LoginInfo, role: Role): DBIO[Boolean] =
      UserQueries.filter(loginInfo).map(_.role).update(role).map(_ != 0)
  }

  override def query = TableQuery[EntityTable]
  override def find(loginInfo: LoginInfo): Future[Option[User]] = db.run(UserActions.find(loginInfo))
  override def create(user: User): Future[User] = db.run(UserActions.create(user))
  override def create(users: Iterable[User]): Future[Seq[User]] = db.run(UserActions.create(users))
  def updateRole(loginInfo: LoginInfo, role: Role): Future[Boolean] = db.run(UserActions.updateRole(loginInfo, role))
}
