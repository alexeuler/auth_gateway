package generators

import models.TokenAction.TokenAction
import models.{Token, TokenAction}
import org.scalacheck.{Arbitrary, Gen}

object TokenGenerators {
  // Generate new token each time (needed for different random token value generation)
  def tokenGen(
     action: TokenAction = TokenAction.Register,
     payload: String = "payload"
  ): Gen[Token] = Gen.choose(0, 0).map { _ =>
    Token(payload = payload, action = action)
  }

  implicit val arbToken = Arbitrary(tokenGen())
}
