package models

import play.api.libs.json._

case class OrderElement(
                    order_element_id: Long,
                    order_id: Long,
                    product_id: Long,
                    quantity: Int,
                    price: Double
                  )

object OrderElement {
  implicit val orderElementFormat = Json.format[Product]
}
