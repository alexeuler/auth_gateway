package models

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.db.slick.DatabaseConfigProvider
import services.{Dates, Encryption}

import scala.concurrent.Future

object TokenAction extends Enumeration {
  type TokenAction = Value
  val Register = Value("register")
  val ForgotPassword = Value("forgot_password")
}

import TokenAction._

case class Token(id: Long = 0L,
                 createdAt: Timestamp = new Timestamp(0L),
                 updatedAt: Timestamp = new Timestamp(0L),
                 payload: String,
                 action: TokenAction,
                 value: String = Encryption.randomHash(),
                 expirationTime: Timestamp = new Timestamp(Dates.daysFromNow(7).getTime))

trait TokenRepo {
  def find(value: String): Future[Option[Token]]
  def create(token: Token): Future[Token]
}

@Singleton
class TokenRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)
  extends BaseRepo[Token](dbConfigProvider) with TokenRepo {

  import driver.api._

  implicit val tokenActionMapper = MappedColumnType.base[TokenAction, String](
    action => action.toString,
    string => TokenAction.withName(string)
  )

  class EntityTable(tag: Tag) extends BaseTable(tag, "tokens") {
    def payload = column[String]("payload")
    def value = column[String]("value")
    def action = column[TokenAction]("action")
    def expirationTime = column[Timestamp]("expiration_time")

    override def * = (id, createdAt, updatedAt, payload, action, value, expirationTime) <> (Token.tupled, Token.unapply)
  }

  override def query = TableQuery[EntityTable]

  object Queries {
    def filter(value: String): Query[EntityTable, Token, Seq] =
      for (token <- query; if token.value === value) yield token
  }

  object Actions {
    def find(value: String): DBIO[Option[Token]] = Queries.filter(value).result.headOption
  }

  override def find(value: String): Future[Option[Token]] = db.run(Actions.find(value))
}
