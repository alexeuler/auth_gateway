package generators

import models.TokenAction.TokenAction
import models.{Role, Token, TokenAction, User}
import org.scalacheck.{Arbitrary, Gen}

object TokenGenerators {
  // Generate new token each time (needed for different random token value generation)
  def tokenGen(
     action: TokenAction = TokenAction.Register,
     payload: String = "payload"
  ): Gen[Token] = Gen.choose(0, 0).map { _ =>
    Token(payload = payload, action = action)
  }

  def tokenWithUnconfirmedUser: Gen[(Token, User)] = for {
    user <- UserGenerators.userGen(Role.Unconfirmed)
    token <- tokenGen(TokenAction.Register, payload = user.email)
  } yield (token, user)

  implicit val arbToken = Arbitrary(tokenGen())
}
