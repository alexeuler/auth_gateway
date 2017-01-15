package models

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.{AbstractTable, Rep}

import scala.concurrent.Future

trait ModelTable[T] extends AbstractTable[T] {
  def id: Rep[Long]
  def createdAt: Rep[Timestamp]
  def updatedAt: Rep[Timestamp]
}

// Todo adhoc polymorphism
// import slick.driver.JdbcProfile to make Table accessible?
// Or make ModelTable depend on dbConfigProvider?
abstract class ModelRepo[T] @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  abstract class ModelTableImpl(tag: Tag, name: String) extends Table[T](tag, name) with ModelTable[T] {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def createdAt = column[Timestamp]("created_at", O.AutoInc)
    def updatedAt = column[Timestamp]("updated_at", O.AutoInc)
  }

  type U <: ModelTable[T]
//
  def modelsQuery: TableQuery[U]
  def find(id: Long): Future[Option[T]] = db.run(filteredQuery(id).result.headOption)
  def filteredQuery(id: Long): Query[U, T, Seq] = for (entity <- modelsQuery; if entity.id === id) yield { entity }


//  abstract class Queries[U <: ModelTable] {
//    def all: Future[Seq[T]] = db.run(modelsQuery.result)
//    def find(id: Long): Future[Option[T]] = db.run(filterQuery(id).result.headOption)
//    def create(entity: T): Future[Long] = db.run(createQuery(entity))
//
//    protected def modelsQuery: TableQuery[U]
//    private val filterQuery = (id: Long) => for (entity <- modelsQuery; if entity.id === id) yield { entity }
//    private val createQuery = (entity: T) => modelsQuery returning modelsQuery.map(_.id) += entity
//  }
}
