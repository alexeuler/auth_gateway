import helpers.DefaultSpec
import org.scalatestplus.play.OneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder

import scala.collection.mutable

class StackSpec extends DefaultSpec with OneAppPerSuite {
  describe("Stack") {
    it("pops values LIFO") {
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      stack.pop() should === (2)
      stack.pop() should === (1)
    }
  }
}

