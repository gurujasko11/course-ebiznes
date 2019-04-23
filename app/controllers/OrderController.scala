package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class OrderController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def add_order = Action { Ok("add order") }
  def get_order(id: String) = Action { Ok("get order") }
  def get_orders = Action { Ok("get orders") }
  def delete_order(id: String) = Action { Ok("delete order") }
  def edit_order(id: String) = Action { Ok("edit order") }

  def cart = Action { Ok("cart") }
  def add_to_cart(id: String) = Action { Ok("add to cart") }
  def remove_from_cart(id: String) = Action { Ok("remove from cart") }
  def checkout_cart = Action { Ok("checkout cart")}
  def payment(id: String) = Action {Ok("payment")}
  def shipping(id: String) = Action {Ok("shipping")}

}
