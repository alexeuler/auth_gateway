package silhouette

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.{User, UserRepo}

import scala.concurrent.Future

trait UserService extends IdentityService[User] {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]]
}

class UserServiceImpl @Inject() (userRepo: UserRepo) extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepo.find(loginInfo)
}
