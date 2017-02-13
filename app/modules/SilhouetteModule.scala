package modules

import com.google.inject.{AbstractModule, Provides}
import services.UserService
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.crypto.{Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api.util.{Clock, FingerprintGenerator}
import com.mohiva.play.silhouette.impl.authenticators.{SessionAuthenticator, SessionAuthenticatorService, SessionAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import models.User
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import silhouette.DefaultEnv
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.ExecutionContext

class SilhouetteModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[IdentityService[User]].to[UserService]
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def environmentProvider(userService: IdentityService[User],
                          authenticatorService: AuthenticatorService[SessionAuthenticator],
                          eventBus: EventBus
                         )(implicit ex: ExecutionContext) : Environment[DefaultEnv] =
    Environment[DefaultEnv](userService, authenticatorService, Seq(), eventBus)

  @Provides
  def authenticatorProvider(configuration: Configuration,
                            fingerprintGenerator: FingerprintGenerator,
                            crypter: Crypter,
                            clock: Clock
                           )(implicit ex: ExecutionContext): AuthenticatorService[SessionAuthenticator]  = {
    val config = configuration.underlying.as[SessionAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)
    new SessionAuthenticatorService(config, fingerprintGenerator, encoder, clock)
  }

  @Provides
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")
    new JcaCrypter(config)
  }
}
