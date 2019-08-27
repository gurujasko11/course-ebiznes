package controllers

import javax.inject._
import models.UserRepository
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  */
@Singleton
class UserController @Inject()(userRepository: UserRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "login" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> nonEmptyText,
      "phone" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  def login = Action {
    Ok("login")
  }

  def logout = Action {
    Ok("logout")
  }

  def add_user = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create user."))
      },
      user => {
        userRepository.create(
          user.login,
          user.email,
          user.password,
          user.phone
        ).map { user =>
          Created(Json.toJson(user))
        }
      }
    )
  }

  def get_user(id: Long) = Action.async { implicit request =>
    val options = for {
      maybeUser <- userRepository.findById(id)
    } yield (maybeUser)

    options.map { case (opt) =>
      opt match {
        case Some(user) => Ok(Json.toJson(user))
        case None => NotFound
      }
    }
  }

  def get_users = {
    Action.async { implicit request =>
      userRepository.list().map {
        user => Ok(Json.toJson(user))
      }
    }
  }

  def delete_user(id: Long) = Action.async(
    userRepository.delete(id).map(_ => Ok(""))
  )

  def edit_user(id: Long) = Action {
    Ok("edit user")
  }

//  def auth_request(code: Option[String]) = Action {
//    Ok("the code is:" + code)
//    val url = "www.googleapis.com/oauth2/v4/token HTTP/1.1";
//
////    val request: HttpRequest =
//    Http("www.googleapis.com/oauth2/v4/token HTTP/1.1")
//      .postForm(Seq(
//        "code" -> "4/P7q7W91a-oMsCeLvIaQm6bTrgtp7",
////        "client_id" -> code,
////        "client_id" -> code,
//        "redirect_uri" -> "https://localhost:9000/auth_cfm")).asString
//  }

}

case class CreateUserForm(login: String, password: String, email: String, phone: String)
