package controllers

import javax.inject._
import models.OrderElementRepository
import play.api.data.Form
import play.api.data.Forms.{ mapping, _ }
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.format.Formats._

import scala.concurrent.{ ExecutionContext, Future }

/**
 */
@Singleton
class OrderElementController @Inject() (orderElementRepository: OrderElementRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val orderElementForm: Form[CreateOrderElementForm] = Form {
    mapping(
      "order_id" -> of(longFormat),
      "product_id" -> of(longFormat),
      "quantity" -> number,
      "price" -> of(doubleFormat)
    )(CreateOrderElementForm.apply)(CreateOrderElementForm.unapply)
  }
  def add_order_element = Action.async { implicit request =>
    orderElementForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create order element."))
      },
      order_element => {
        orderElementRepository.create(
          order_element.order_id,
          order_element.product_id,
          order_element.quantity,
          order_element.price
        ).map { order_element =>
            Created(Json.toJson(order_element))
          }
      }
    )
  }
  def get_order_element(id: Long) = Action.async { implicit request =>
    val options = for {
      maybeOrderElement <- orderElementRepository.findById(id)
    } yield (maybeOrderElement)

    options.map {
      case (opt) =>
        opt match {
          case Some(orderElement) => Ok(Json.toJson(orderElement))
          case None => NotFound
        }
    }
  }

  def get_order_elements = {
    Action.async { implicit request =>
      orderElementRepository.list().map {
        orderElement => Ok(Json.toJson(orderElement))
      }
    }
  }

  def delete_order_element(id: Long) = Action.async(
    orderElementRepository.delete(id).map(_ => Ok(""))
  )

  def edit_order_element(id: Long) = Action { Ok("edit user") }

}
case class CreateOrderElementForm(order_id: Long, product_id: Long, quantity: Int, price: Double)
