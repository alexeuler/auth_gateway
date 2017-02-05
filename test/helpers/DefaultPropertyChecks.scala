package helpers

import org.scalatest.prop
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}

import scala.concurrent.{Await, Future}


trait DefaultPropertyChecks extends PropertyChecks with AsyncPropertyChecks {
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = 5, minSize = 0, sizeRange = 10)
}
