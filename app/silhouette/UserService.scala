package silhouette

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.{User, UserRepo}

import scala.concurrent.Future

class UserService @Inject() (userRepo: UserRepo) extends IdentityService[User] {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepo.find(loginInfo)
}
