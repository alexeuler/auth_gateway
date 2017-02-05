package models

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import models.TokenAction.TokenAction
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait TokenRepo {
  def find(value: String): Future[Option[Token]]
  def create(token: Token): Future[Token]
  def delete(id: Long): Future[Int]
}

@Singleton
class TokenRepoImpl @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext)
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

