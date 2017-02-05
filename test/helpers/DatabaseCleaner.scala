package helpers

import play.api.Application
import play.api.db.DBApi
import org.scalatest._
import play.api.db.evolutions.Evolutions

trait DatabaseCleaner {
  this: BeforeAndAfter =>

  val app: Application
  val database = app.injector.instanceOf(classOf[DBApi]).database("default")

  def cleanDatabase: Unit = {
    Evolutions.cleanupEvolutions(database)
    Evolutions.applyEvolutions(database)
  }

  before {
    cleanDatabase
  }
}
