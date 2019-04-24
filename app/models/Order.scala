package models

import play.api.libs.json._

case class Order(
                    order_id: Long,
                    address_id: Long,
                    order_date: String,
                    realisation_date: String
                  )

object Order {
  implicit val orderFormat = Json.format[Product]
}
