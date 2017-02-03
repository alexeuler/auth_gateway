package helpers

import org.scalatest.prop.PropertyChecks

/**
  * Created by alex on 04/02/17.
  */
trait DefaultPropertyChecks extends PropertyChecks {
  implicit override val generatorDrivenConfig =
    PropertyCheckConfiguration(minSuccessful = 5, minSize = 0, sizeRange = 10)
}
