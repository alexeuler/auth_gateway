package models

import java.sql.Timestamp

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Base class for any repo in the app
  *
  * Repo classes follows the following conventions:
  *   - find - finds zero or one entity using option as a result. If more that one entity is found
  *            TooManyFound exceptions is raised
  *
  *   - filter - same as found, but finds zero or more entities and doesn't raise an exception
  *
  * @param dbConfigProvider
  * @param exec
  * @tparam T
  */



abstract class BaseRepo[T](protected val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import ModelsExceptions._

  type EntityTable <: BaseTable
  def query: TableQuery[EntityTable]

  abstract class BaseTable(tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at", O.AutoInc)
    def updatedAt = column[Timestamp]("updated_at", O.AutoInc)
  }

  protected object BaseQueries {
    def filterById(id: Long): Query[EntityTable, T, Seq] = for (entity <- query; if entity.id === id) yield { entity }
  }

  protected object BaseActions {
    def find(id: Long): DBIO[Option[T]] = BaseQueries.filterById(id).result.flatMap(entities =>
      if (entities.size < 2) DBIO.successful(entities.headOption) else DBIO.failed(TooManyFoundException(id))
    )
    def create(model: T): DBIO[T] = query returning query += model
    def create(models: Iterable[T]): DBIO[Seq[T]] = query returning query ++= models
    def delete(id: Long): DBIO[Int] = BaseQueries.filterById(id).delete
    def clean: DBIO[Int] = query.delete
  }

  def find(id: Long): Future[Option[T]] = db.run(BaseActions.find(id))
  def create(model: T): Future[T] = db.run(BaseActions.create(model))
  def create(models: Iterable[T]): Future[Seq[T]] = db.run(BaseActions.create(models))
  def delete(id: Long): Future[Int] = db.run(BaseActions.delete(id))
  def clean: Future[Int] = db.run(BaseActions.clean)
}
