package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

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

@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "products") {
    def product_id = column[Long]("product_id", O.PrimaryKey, O.AutoInc)
    def category_id = column[Long]("category_id")
    def name = column[String]("name")
    def description = column[String]("description")
    def country_of_origin = column[String]("country_of_origin")
    def weight = column[Int]("weight")
    def price = column[Double]("price")

    def * = (product_id, category_id, name, description, country_of_origin, weight, price) <> ((Product.apply _).tupled, Product.unapply)
  }

  val product = TableQuery[ProductTable]

  def create(category_id: Long, name: String, description: String, country_of_origin: String, weight: Int, price: Double): Future[Product] = db.run {
    (product.map(p => (p.category_id, p.name, p.description, p.country_of_origin, p.weight, p.price))
      returning product.map(_.product_id)
      into {
        case ((category_id, name, description, country_of_origin, weight, price), product_id) =>
          Product(product_id, category_id, name, description, country_of_origin, weight, price)
      }
    ) += ((category_id, name, description, country_of_origin, weight, price))
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (product.filter(_.product_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[Product]] = db.run {
    product.filter(_.product_id === id).result.headOption
  }
}