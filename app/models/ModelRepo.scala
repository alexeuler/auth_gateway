package models

import java.sql.Timestamp

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

abstract class ModelRepo[T](protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  type ModelTable <: BasicTable
  def query: TableQuery[ModelTable]

  abstract class BasicTable(tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at", O.AutoInc)
    def updatedAt = column[Timestamp]("updated_at", O.AutoInc)
  }

  protected object queries {
    def filterById(id: Long): Query[ModelTable, T, Seq] = for (entity <- query; if entity.id === id) yield { entity }
  }

  protected object actions {
    def find(id: Long): DBIO[Option[T]] = queries.filterById(id).result.headOption
    def create(model: T): DBIO[T] = query returning query += model
    def create(models: Seq[T]): DBIO[Seq[T]] = query returning query ++= models
  }

  def find(id: Long): Future[Option[T]] = db.run(actions.find(id))
  def create(model: T): Future[T] = db.run(actions.create(model))
  def create(models: Seq[T]): Future[Seq[T]] = db.run(actions.create(models))
}
