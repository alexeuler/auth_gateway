package models

import java.sql.Timestamp

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

abstract class BaseRepo[T](protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

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
    def find(id: Long): DBIO[Option[T]] = BaseQueries.filterById(id).result.headOption
    def create(model: T): DBIO[T] = query returning query += model
    def create(models: Seq[T]): DBIO[Seq[T]] = query returning query ++= models
    def delete(id: Long): DBIO[Int] = BaseQueries.filterById(id).delete
  }

  def find(id: Long): Future[Option[T]] = db.run(BaseActions.find(id))
  def create(model: T): Future[T] = db.run(BaseActions.create(model))
  def create(models: Seq[T]): Future[Seq[T]] = db.run(BaseActions.create(models))
  def delete(id: Long): Future[Int] = db.run(BaseActions.delete(id))
}
