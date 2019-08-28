package models.services

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class MyOption(
  option_id: Long,
  option_group_id: Long,
  option_value: String
)

@Singleton
class MyOptionRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MyOptionTable(tag: Tag) extends Table[MyOption](tag, "option") {
    def option_id = column[Long]("option_id", O.PrimaryKey, O.AutoInc)
    def option_group_id = column[Long]("option_group_id")
    def option_value = column[String]("option_value")

    def * = (option_id, option_group_id, option_value) <> ((MyOption.apply _).tupled, MyOption.unapply)
  }

  val option = TableQuery[MyOptionTable]

  def create(option_group_id: Long, option_value: String): Future[MyOption] = db.run {
    (option.map(o => (o.option_group_id, o.option_value))
      returning option.map(_.option_id)
      into {
        case ((option_group_id, option_value), option_id) =>
          MyOption(option_id, option_group_id, option_value)
      }
    ) += ((option_group_id, option_value))
  }

  def list(): Future[Seq[MyOption]] = db.run {
    option.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (option.filter(_.option_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[Option[MyOption]] = db.run {
    option.filter(_.option_id === id).result.headOption
  }
}