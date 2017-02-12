package models

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.Role.Role
import play.api.db.slick.DatabaseConfigProvider
import services.{Dates, Encryption}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object TokenAction extends Enumeration {
  type TokenAction = Value
  val Register = Value("register")
  val ForgotPassword = Value("forgot_password")
}

import TokenAction._

case class Token(id: Long = 0L,
                 createdAt: Timestamp = new Timestamp(0L),
                 updatedAt: Timestamp = new Timestamp(0L),
                 payload: String,
                 action: TokenAction,
                 value: String = Encryption.randomHash(),
                 expirationTime: Timestamp = new Timestamp(Dates.daysFromNow(7).getTime)
                )
