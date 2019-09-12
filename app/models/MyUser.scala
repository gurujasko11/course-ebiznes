package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

case class MyUser(
  user_id: Int,
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
    def user_id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)
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

  def delete(id: Int): Future[Unit] = db.run {
    (user.filter(_.user_id === id).delete).map(_ => ())
  }

  def findById(id: Int): Future[scala.Option[MyUser]] = db.run {
    user.filter(_.user_id === id).result.headOption
  }

  def isEmailExist(user_email: String): Future[Boolean] = db.run {
    user.filter(_.email === user_email).exists.result
  }

  def getByEmail(email: String): Future[Seq[MyUser]] = db.run {
    user.filter(_.email === email).result
  }

  def getByEmailAndPassword(user_email: String, user_password: String): Future[Seq[MyUser]] = db.run {
    user.filter(user => (user.email === user_email) && (user.password === user_password)).result
  }

}