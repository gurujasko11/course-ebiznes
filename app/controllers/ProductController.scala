package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class ProductController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def add_product = Action { Ok("add product") }
  def get_product(id: String) = Action { Ok("get product") }
  def get_products = Action { Ok("get products")}
  def delete_product(id: String) = Action { Ok("delete product") }
  def edit_product(id: String) = Action { Ok("edit product") }
  def find_products = Action { Ok("find products") }

}
