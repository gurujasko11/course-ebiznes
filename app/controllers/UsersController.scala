package controllers

import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import models.{ MyUser, MyUserRepository }
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints.{ max, min }
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import utils.auth.DefaultEnv

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future }

@Singleton
class UsersController @Inject() (
  userRepository: MyUserRepository,
  cc: MessagesControllerComponents, silhouette: Silhouette[DefaultEnv]
)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  val logger: Logger = Logger(this.getClass())

  val userRegistrationForm: Form[RegistrationUserForm] = Form {
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email,
      "phone" -> nonEmptyText
    )(RegistrationUserForm.apply)(RegistrationUserForm.unapply)
  }

  val userLoginForm: Form[LoginUserForm] = Form {
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(LoginUserForm.apply)(LoginUserForm.unapply)
  }

  def register = Action.async { implicit request =>
    val login = request.body.asJson.get("login").as[String]
    val password = request.body.asJson.get("password").as[String]
    val email = request.body.asJson.get("email").as[String]
    val phone = request.body.asJson.get("phone").as[String]

    userRepository.create(login, password, email, phone).map { resultMap =>
      Ok("User created").withHeaders("Access-Control-Allow-Origin" -> "*")
    }.recover {
      case ex: Exception => {
        println("Exception in create:" + ex)
        Ok("User exist")
      }
    }
  }

  def login = Action { implicit request =>
    val provided_email = request.body.asJson.get("email").as[String]
    val provided_password = request.body.asJson.get("password").as[String]

    println("Provided email " + provided_email + " provided password " + provided_password)
    val user = userRepository.getByEmailAndPassword(provided_email, provided_password)

    Await.result(user, Duration.Inf)
    val userResult: Seq[MyUser] = user.value.get.get

    println("result user " + userResult)
    Ok(Json.toJson(userResult)).withHeaders(
      "Access-Control-Allow-Origin" -> "*")
  }

  def signOut = Action { implicit request =>
    Ok(views.html.home())
  }

  //  def updateUser(id: Long) = Action.async { implicit request =>
  //    val login = request.body.asJson.get("login").as[String]
  //    val password = request.body.asJson.get("password").as[String]
  //    val user_email = request.body.asJson.get("user_email").as[String]
  //    val phone = request.body.asJson.get("phone").as[String]
  //    //TODO
  //    userRepository.update(id, login, password, user_email, phone).map { product =>
  //      Ok(Json.toJson(product)).withHeaders(
  //        "Access-Control-Allow-Origin" -> "*")
  //    }
  //  }

  def deleteUser(id: Int) = Action.async { implicit request =>
    userRepository.delete(id).map { user =>
      Ok("Succesfully removed user")
    }
  }

  def getUsers = Action.async { implicit request =>
    userRepository.list().map { users =>
      Ok(Json.toJson(users)).withHeaders(
        "Access-Control-Allow-Origin" -> "*")
    }
  }
}

case class RegistrationUserForm(login: String, password: String, email: String, phone: String)

case class LoginUserForm(email: String, password: String)
