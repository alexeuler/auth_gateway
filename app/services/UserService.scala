package services

import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.{User, UserRepo}

import scala.concurrent.Future

trait UserService extends IdentityService[User] {
  def retrieve(loginInfo: LoginInfo): Future[Option[User]]
  def create(user: User): Future[User]
}

@Singleton
class UserServiceImpl @Inject()(userRepo: UserRepo) extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepo.find(loginInfo)
  override def create(user: User): Future[User] = userRepo.create(user)
}
