package models

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.Future

abstract class Model {
  def id: Long = 0L
  def createdAt: Timestamp = new Timestamp(0L)
  def updatedAt: Timestamp = new Timestamp(0L)
}

// Todo adhoc polymorphism
abstract class Models[T <: Model] @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  abstract class ModelTable(tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at", O.AutoInc)
    def updatedAt = column[Timestamp]("updated_at", O.AutoInc)
  }

  abstract class Queries[U <: ModelTable] {
    def all: Future[Seq[T]] = db.run(modelsQuery.result)
    def find(id: Long): Future[Option[T]] = db.run(filterQuery(id).result.headOption)
    def create(entity: T): Future[Long] = db.run(createQuery(entity))

    protected def modelsQuery: TableQuery[U]
    private val filterQuery = (id: Long) => for (entity <- modelsQuery; if entity.id === id) yield { entity }
    private val createQuery = (entity: T) => modelsQuery returning modelsQuery.map(_.id) += entity
  }
}
