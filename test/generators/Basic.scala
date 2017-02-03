package generators

import generators.fixtures.Fixtures
import org.scalacheck.Gen

object Basic {
  val emailGen: Gen[String] = for {
    firstName <- Gen.oneOf(Fixtures.firstNames)
    lastName <- Gen.oneOf(Fixtures.lastNames)
    emailProvider <- Gen.oneOf(Fixtures.emailProviders)
  } yield s"$firstName.$lastName@$emailProvider"

  val smallNumGen: Gen[Int] = Gen.choose(0, 9)
}
