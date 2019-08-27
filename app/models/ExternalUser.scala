package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.json.Json

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class ExternalUser(
                 userID: UUID,
                 loginInfo: LoginInfo,
                 firstName: scala.Option[String],
                 lastName: scala.Option[String],
                 fullName: scala.Option[String],
                 email: scala.Option[String],
                 avatarURL: scala.Option[String]) extends Identity {

  object ExternalUser {
    implicit val categoryFormat = Json.format[ExternalUser]
  }
  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}
