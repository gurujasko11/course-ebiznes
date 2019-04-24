package models

import play.api.libs.json._

case class Product(
                    product_id: Long,
                    category_id: Long,
                    name: String,
                    description: String,
                    country_of_origin: String,
                    weight: Int,
                    price: Double
                  )

object Product {
  implicit val productFormat = Json.format[Product]
}
