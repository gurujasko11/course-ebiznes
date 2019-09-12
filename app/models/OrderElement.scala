package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class OrderElement(
  order_element_id: Int,
  order_id: Int,
  product_id: Int,
  quantity: Int,
  price: Double
)

object OrderElement {
  implicit val orderElementFormat = Json.format[OrderElement]
}

@Singleton
class OrderElementRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderElementTable(tag: Tag) extends Table[OrderElement](tag, "orders_elements") {
    def order_element_id = column[Int]("order_element_id", O.PrimaryKey, O.AutoInc)
    def order_id = column[Int]("order_id")
    def product_id = column[Int]("product_id")
    def quantity = column[Int]("quantity")
    def price = column[Double]("price")

    def * = (order_element_id, order_id, product_id, quantity, price) <> ((OrderElement.apply _).tupled, OrderElement.unapply)
  }

  val orderElement = TableQuery[OrderElementTable]

  def create(order_id: Int, product_id: Int, quantity: Int, price: Double): Future[OrderElement] = db.run {
    (orderElement.map(o => (o.order_id, o.product_id, o.quantity, o.price))
      returning orderElement.map(_.order_element_id)
      into {
        case ((order_id, product_id, quantity, price), order_element_id) =>
          OrderElement(order_element_id, order_id, product_id, quantity, price)
      }
    ) += ((order_id, product_id, quantity, price))

  }

  def list(): Future[Seq[OrderElement]] = db.run {
    orderElement.result
  }

  def delete(id: Int): Future[Unit] = db.run {
    (orderElement.filter(_.order_element_id === id).delete).map(_ => ())
  }

  def findById(id: Int): Future[scala.Option[OrderElement]] = db.run {
    orderElement.filter(_.order_element_id === id).result.headOption
  }
}