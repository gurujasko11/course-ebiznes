package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class MyUser(
  user_id: Long,
  login: String,
  password: String,
  email: String,
  phone: String
)

object MyUser {
  implicit val userFormat = Json.format[MyUser]
}

@Singleton
class MyUserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MyUserTable(tag: Tag) extends Table[MyUser](tag, "users") {
    def user_id = column[Long]("user_id", O.PrimaryKey, O.AutoInc)
    def login = column[String]("login")
    def password = column[String]("password")
    def email = column[String]("email")
    def phone = column[String]("phone")

    def * = (user_id, login, password, email, phone) <> ((MyUser.apply _).tupled, MyUser.unapply)
  }

  val user = TableQuery[MyUserTable]

  def create(login: String, password: String, email: String, phone: String): Future[MyUser] = db.run {
    (user.map(u => (u.login, u.password, u.email, u.phone))
      returning user.map(_.user_id)
      into {
        case ((login, password, email, phone), user_id) =>
          MyUser(user_id, login, password, email, phone)
      }
    ) += ((login, password, email, phone))
  }

  def list(): Future[Seq[MyUser]] = db.run {
    user.result
  }

  def delete(id: Long): Future[Unit] = db.run {
    (user.filter(_.user_id === id).delete).map(_ => ())
  }

  def findById(id: Long): Future[scala.Option[MyUser]] = db.run {
    user.filter(_.user_id === id).result.headOption
  }
}