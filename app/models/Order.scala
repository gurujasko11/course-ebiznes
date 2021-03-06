package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class Order(
  order_id: Int,
  address_id: Int,
  order_date: String,
  realisation_date: String
)

object Order {
  implicit val orderFormat = Json.format[Order]
}

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "orders") {
    def order_id = column[Int]("order_id", O.PrimaryKey, O.AutoInc)
    def address_id = column[Int]("address_id")
    def order_date = column[String]("order_date")
    def realisation_date = column[String]("realisation_date")

    def * = (order_id, address_id, order_date, realisation_date) <> ((Order.apply _).tupled, Order.unapply)
  }

  val order = TableQuery[OrderTable]

  def create(address_id: Int, order_date: String, realisation_date: String): Future[Order] = db.run {
    (order.map(o => (o.address_id, o.order_date, o.realisation_date))
      returning order.map(_.order_id)
      into {
        case ((address_id, order_date, realisation_date), order_id) =>
          Order(order_id, address_id, order_date, realisation_date)
      }
    ) += ((address_id, order_date, realisation_date))
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def delete(id: Int): Future[Unit] = db.run {
    (order.filter(_.order_id === id).delete).map(_ => ())
  }

  def findById(id: Int): Future[scala.Option[Order]] = db.run {
    order.filter(_.order_id === id).result.headOption
  }

  def update(newValue: Order) = db.run {
    order.insertOrUpdate(newValue)
  }

}