package models

import play.api.libs.json._

case class Category(category_id: Long, name: String)

object Category {
  implicit val categoryFormat = Json.format[Category]
}
