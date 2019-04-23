package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class CategoryController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def add_category = Action { Ok("add category") }
  def get_category(id: String) = Action { Ok("get category") }
  def get_categories = Action { Ok("get categories") }
  def delete_category(id: String) = Action { Ok("delete category") }
  def edit_category(id: String) = Action { Ok("edit category") }

}
