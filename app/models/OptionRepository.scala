package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OptionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class OptionTable(tag: Tag) extends Table[Option](tag, "option") {
    def option_id = column[Long]("option_id", O.PrimaryKey, O.AutoInc)
    def option_group_id = column[Long]("option_group_id")
    def option_value = column[String]("option_value")

    def * = (option_id, option_group_id, option_value) <> ((Option.apply _).tupled, Option.unapply)
  }

  val option = TableQuery[OptionTable]

    def create(option_group_id: Long, option_value: String): Future[models.Option] = db.run {
      (option.map(o => (o.option_group_id, o.option_value))
        returning option.map(_.option_id)
        into {case((option_group_id, option_value), option_id) =>
        models.Option(option_id, option_group_id, option_value)}
        ) += (option_group_id, option_value)
    }

  def list(): Future[Seq[Option]] = db.run {
    option.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (option.filter(_.option_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[models.Option]] = db.run {
    option.filter(_.option_id === id).result.headOption
  }
}