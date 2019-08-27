package authUtils

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.ExternalUser

/**
 * The default env.
 */
trait DefaultEnv extends Env {
  type I = ExternalUser
  type A = JWTAuthenticator
}
