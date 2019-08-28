package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class Category(
  category_id: Long,
  name: String)

object Category {
  implicit val categoryFormat = Json.format[Category]
}

@Singleton
class CategoryRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "categories") {
    def category_id = column[Long]("category_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("category_name")
    def * = (category_id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val category = TableQuery[CategoryTable]

  def create(name: String): Future[Category] = db.run {
    (category.map(c => (c.name))
      returning category.map(_.category_id)
      into { case ((name), category_id) => Category(category_id, name) }
    ) += ((name))
  }

  def list(): Future[Seq[Category]] = db.run {
    category.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (category.filter(_.category_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[Category]] = db.run {
    category.filter(_.category_id === id).result.headOption
  }

  def update(newValue: Category) = db.run {
    category.insertOrUpdate(newValue)
  }

}