package helpers

import org.scalatest.{AsyncFunSpec, FunSpec, Matchers, OptionValues}
import org.scalatestplus.play.WsScalaTestClient

abstract class AsyncDefaultSpec extends AsyncFunSpec
  with Matchers
  with OptionValues
  with WsScalaTestClient

