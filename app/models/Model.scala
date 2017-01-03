package models

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.profile.RelationalTableComponent
import slick.lifted.{AbstractTable, ProvenShape}

import scala.concurrent.Future

abstract class Model[T] {
  def id: Option[Long] = None
  def createdAt: Option[Timestamp] = None
  def updatedAt: Option[Timestamp] = None
  def setTimeStamps(createdAt: Option[Timestamp], updatedAt: Option[Timestamp]): T
}

abstract class Models[T <: Model[T], U <: Models[T, U]#ModelTable] @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  protected abstract class ModelTable(tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at")
    def updatedAt = column[Timestamp]("updated_at")
  }
  
  def all: Future[Seq[T]] = db.run(allQuery.result)
  def find(id: Long): Future[Option[T]] = db.run(filterQuery(id).result.headOption)
  def create(entity: T): Future[Long] = db.run(createQuery(entity))

  def allQuery: TableQuery[U]

  private val filterQuery = (id: Long) => for (entity <- allQuery; if entity.id === id) yield { entity }
  private val createQuery = (entity: T) => {
    val now = Some(new Timestamp(new Date().getTime))
    val entityWithTimestamp: T = entity.setTimeStamps(now, now)
    (allQuery returning allQuery.map(_.id)) += entityWithTimestamp
  }
}
