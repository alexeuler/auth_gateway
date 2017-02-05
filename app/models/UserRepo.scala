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
  def updateRole(loginInfo: LoginInfo, role: Role): Future[Int]
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
  }

  object UserActions {
    def find(loginInfo: LoginInfo): DBIO[Option[User]] = UserQueries.filter(loginInfo).result.flatMap(users =>
      if (users.size < 2) DBIO.successful(users.headOption) else DBIO.failed(TooManyFoundException(loginInfo))
    )
    def updateRole(loginInfo: LoginInfo, role: Role): DBIO[Int] =
      UserQueries.filter(loginInfo).map(_.role).update(role)
  }

  override def query = TableQuery[EntityTable]

  override def find(loginInfo: LoginInfo): Future[Option[User]] = db.run(UserActions.find(loginInfo))
  def updateRole(loginInfo: LoginInfo, role: Role): Future[Int] = db.run(UserActions.updateRole(loginInfo, role))
}
