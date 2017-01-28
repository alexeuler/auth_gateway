package models

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.db.slick.DatabaseConfigProvider
import services.{Dates, Encryption}

import scala.concurrent.Future

case class UserRegisterToken(id: Long = 0L,
                             createdAt: Timestamp = new Timestamp(0L),
                             updatedAt: Timestamp = new Timestamp(0L),
                             email: String,
                             value: String = Encryption.randomHash(),
                             expirationTime: Timestamp = new Timestamp(Dates.daysFromNow(7).getTime))

trait UserRegisterTokenRepo {
  def find(value: String): Future[Option[UserRegisterToken]]
}

@Singleton
class UserRegisterTokenRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[UserRegisterToken](dbConfigProvider) with UserRegisterTokenRepo {

  import driver.api._

  class EntityTable(tag: Tag) extends BaseTable(tag, "users") {
    def email = column[String]("email")
    def value = column[String]("value")
    def expirationTime = column[Timestamp]("expiration_time", O.AutoInc)

    override def * = (id, createdAt, updatedAt, email, value, expirationTime) <> (UserRegisterToken.tupled, UserRegisterToken.unapply)
  }

  override def query = TableQuery[EntityTable]

  object Queries {
    def filter(value: String): Query[EntityTable, UserRegisterToken, Seq] =
      for (token <- query; if token.value === value) yield token
  }

  object Actions {
    def find(value: String): DBIO[Option[UserRegisterToken]] = Queries.filter(value).result.headOption
  }

  override def find(value: String): Future[Option[UserRegisterToken]] = db.run(Actions.find(value))
}
