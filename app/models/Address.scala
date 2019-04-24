package models

import play.api.libs.json.Json

case class Address(
                    address_id: Long,
                    user_id: Long,
                    country: String,
                    city: String,
                    street: String,
                    home_number: Int,
                    apartament_number: Int,
                    postal_code: String
                  )

object Address {
  implicit val addressFormat = Json.format[Category]
}
