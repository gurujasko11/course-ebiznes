package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def login = Action { Ok("login") }
  def logout = Action { Ok("logout") }

  def add_user = Action { Ok("add user") }
  def get_user(id: String) = Action { Ok("get user")}
  def get_users = Action { Ok("get users")}
  def delete_user(id: String) = Action { Ok("delete user") }
  def edit_user(id: String) = Action { Ok("edit user") }

  def add_address = Action { Ok("create address") }
  def get_address(id: String) = Action { Ok("get address")}
  def get_addresses = Action { Ok("get addresses")}
  def delete_address(id: String) = Action { Ok("delete address") }
  def edit_address(id: String) = Action { Ok("edit address") }

}
