package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Address(
                    address_id: Long,
                    user_id: Long,
                    country: String,
                    city: String,
                    street: String,
                    home_number: Int,
                    apartament_number: scala.Option[Int],
                    postal_code: String
                  )

object Address {
  implicit val addressFormat = Json.format[Address]
}

@Singleton
class AddressRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class AddressTable(tag: Tag) extends Table[Address](tag, "addresses") {
    def address_id = column[Long]("address_id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def country = column[String]("country")
    def city = column[String]("city")
    def street = column[String]("street")
    def home_number = column[Int]("home_number")
    def apartament_number = column[scala.Option[Int]]("apartament_number")
    def postal_code = column[String]("postal_code")

    def * = (address_id, user_id, country, city, street, home_number, apartament_number, postal_code) <> ((Address.apply _).tupled, Address.unapply)
  }

  val address = TableQuery[AddressTable]

  def create(user_id: Long, country: String, city: String, street: String, home_number: Int, apartament_number: scala.Option[Int], postal_code: String): Future[Address] = db.run {
    (address.map(a => (a.user_id, a.country, a.city, a.street, a.home_number, a.apartament_number, a.postal_code))
      returning address.map(_.address_id)
      into {case((user_id, country, city, street, home_number, apartament_number, postal_code), address_id) =>
      Address(address_id, user_id, country, city, street, home_number, apartament_number, postal_code)}
      ) += (user_id, country, city, street, home_number, apartament_number, postal_code)
  }

  def list(): Future[Seq[Address]] = db.run {
    address.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (address.filter(_.address_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[Address]] = db.run {
    address.filter(_.address_id === id).result.headOption
  }

  def update(newValue: Address) = db.run{
    address.insertOrUpdate(newValue)
  }
}