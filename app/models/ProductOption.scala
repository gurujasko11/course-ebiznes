package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class ProductOption(
  product_option_id: Long,
  product_id: Long,
  option_id: Long,
  option_group_id: Long
)

@Singleton
class ProductOptionRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductOptionTable(tag: Tag) extends Table[ProductOption](tag, "product_option") {
    def product_option_id = column[Long]("product_option_id", O.PrimaryKey, O.AutoInc)
    def product_id = column[Long]("product_id")
    def option_id = column[Long]("option_id")
    def option_group_id = column[Long]("option_group_id")

    def * = (product_option_id, product_id, option_id, option_group_id) <> ((ProductOption.apply _).tupled, ProductOption.unapply)
  }

  val product_option = TableQuery[ProductOptionTable]

  def create(product_id: Long, option_id: Long, option_group_id: Long): Future[ProductOption] = db.run {
    (product_option.map(p => (p.product_id, p.option_id, p.option_group_id))
      returning product_option.map(_.product_option_id)
      into {
        case ((product_id, option_id, option_group_id), product_option_id) =>
          ProductOption(product_option_id, product_id, option_id, option_group_id)
      }
    ) += ((product_id, option_id, option_group_id))
  }

  def list(): Future[Seq[ProductOption]] = db.run {
    product_option.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (product_option.filter(_.product_option_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[ProductOption]] = db.run {
    product_option.filter(_.product_option_id === id).result.headOption
  }
}