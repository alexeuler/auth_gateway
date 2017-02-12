package helpers

import org.scalatest._
import org.scalatestplus.play.WsScalaTestClient

abstract class AsyncDefaultSpec extends AsyncFunSpec
  with Matchers
  with OptionValues
  with WsScalaTestClient
  with BeforeAndAfter

