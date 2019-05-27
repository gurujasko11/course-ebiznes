package controllers

import javax.inject._
import models.OrderRepository
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.format.Formats._

import scala.concurrent.{ExecutionContext, Future}

/**
  */
@Singleton
class OrderController @Inject()(orderRepository: OrderRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "address_id" -> of(longFormat),
      "order_date" -> nonEmptyText,
      "realisation_date" -> nonEmptyText
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  def add_order = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create order."))
      },
      order => {
        orderRepository.create(
          order.address_id,
          order.order_date,
          order.realisation_date
        ).map { order =>
          Created(Json.toJson(order))
        }
      }
    )
  }
  def get_order(id: Long) = Action.async { implicit request =>
    val options = for {
      maybeOrder <- orderRepository.findById(id)
    } yield (maybeOrder)

    options.map { case (opt) =>
      opt match {
        case Some(order) => Ok(Json.toJson(order))
        case None => NotFound
      }
    }
  }

  def get_orders = {
    Action.async { implicit request =>
      orderRepository.list().map{
        order => Ok(Json.toJson(order))
      }
    }
  }

  def delete_order(id: Long) = Action.async(
    orderRepository.delete(id).map(_ => Ok(""))
  )

  def edit_order(id: Long) = Action { Ok("edit order") }

  def cart = Action { Ok("cart") }
  def add_to_cart(id: String) = Action { Ok("add to cart") }
  def remove_from_cart(id: String) = Action { Ok("remove from cart") }
  def checkout_cart = Action { Ok("checkout cart")}
  def payment(id: String) = Action {Ok("payment")}
  def shipping(id: String) = Action {Ok("shipping")}

}
case class CreateOrderForm(address_id: Long, order_date: String, realisation_date: String)
