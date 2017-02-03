package helpers

import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient

abstract class DefaultSpec extends FunSpec with Matchers with OptionValues with WsScalaTestClient

