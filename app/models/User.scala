package models

import play.api.libs.json._

case class User(
                    user_id: Long,
                    login: String,
                    password: String,
                    email: String,
                    phone: String
                  )

object User {
  implicit val userFormat = Json.format[User]
}
