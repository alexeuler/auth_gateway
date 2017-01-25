package silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.User

class DefaultEnv extends Env {
  type I = User
  type A = SessionAuthenticator
}
