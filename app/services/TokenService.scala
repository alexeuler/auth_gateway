package services

import cats.data.OptionT
import cats.implicits._
import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import models.{Role, TokenRepo, UserRepo}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait TokenService {
  def handle(value: String): Future[Boolean]
}

@Singleton
class TokenServiceImpl @Inject()(
                                  userRepo: UserRepo,
                                  tokenRepo: TokenRepo
                                )
                                (
                                  implicit exec: ExecutionContext
                                )extends TokenService {

  override def handle(value: String): Future[Boolean] = (
    for {
      token <- OptionT(tokenRepo.find(value))
      user <- OptionT(userRepo.find(new LoginInfo("email", token.payload)))
      _ <- OptionT.liftF(userRepo.updateRole(user.toLoginInfo, Role.User))
      deletedCount <- OptionT.liftF(tokenRepo.delete(value))
    } yield deletedCount == 1
    ).getOrElse(false).recover {
    case NonFatal(_) => false
  }

}
