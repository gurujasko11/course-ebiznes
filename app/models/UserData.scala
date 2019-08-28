package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }
import slick.jdbc.MySQLProfile.api._

case class UserData(id: Int, provider: String, user_key: String)

object UserData {
  implicit val categoryFormat = Json.format[UserData]
}

class UserDataTableDef(tag: Tag) extends Table[UserData](tag, "UserData") {

  def id = column[Int]("id", O.PrimaryKey)
  def provider = column[String]("provider")
  def user_key = column[String]("user_key")

  override def * =
    (id, provider, user_key) <> ((UserData.apply _).tupled, UserData.unapply)
}

case class UserDataFormData(provider: String, user_key: String)

object UserDataForm {
  val form = Form(
    mapping(
      "provider" -> nonEmptyText,
      "user_key" -> nonEmptyText
    )(UserDataFormData.apply)(UserDataFormData.unapply)
  )
}

class UserDataRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val externalUserDataRepository = TableQuery[UserDataTableDef]

  def add(externalUserData: UserData): Future[String] = {
    dbConfig.db.run(externalUserDataRepository += externalUserData).map(res => "Own user successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(externalUserDataRepository.filter(_.id === id).delete)
  }

  def get(id: Int): Future[scala.Option[UserData]] = {
    dbConfig.db.run(externalUserDataRepository.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[UserData]] = {
    dbConfig.db.run(externalUserDataRepository.result)
  }

  def getByProvider(provider: String, user_key: String): Future[scala.Option[UserData]] = {
    dbConfig.db.run(externalUserDataRepository.filter(_.provider === provider).filter(_.user_key === user_key).result.headOption)
  }

}