package controllers

import javax.inject._
import models.AddressRepository
import play.api.data.Form
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.format.Formats._

import scala.concurrent.{ExecutionContext, Future}

/**
  */
@Singleton
class AddressController @Inject()(addressRepository: AddressRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val addressForm: Form[CreateAddressForm] = Form {
    mapping(
      "user_id" -> of(longFormat),
      "country" -> nonEmptyText,
      "city" -> nonEmptyText,
      "street"-> nonEmptyText,
      "home_number"-> number,
      "apartament_number"-> number,
      "postal_code" -> nonEmptyText
    )(CreateAddressForm.apply)(CreateAddressForm.unapply)
  }

  def add_address  = Action.async { implicit request =>
    addressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create address."))
      },
      address => {
        addressRepository.create(
          address.user_id,
          address.country,
          address.city,
          address.street,
          address.home_number,
          address.apartament_number,
          address.postal_code
        ).map { address =>
          Created(Json.toJson(address))
        }
      }
    )
  }

  def get_address(id: Long) = Action.async { implicit request =>
    val options = for {
      maybeAddress <- addressRepository.findById(id)
    } yield (maybeAddress)

    options.map { case (opt) =>
      opt match {
        case Some(address) => Ok(Json.toJson(address))
        case None => NotFound
      }
    }
  }

  def get_addresses = {
    Action.async { implicit request =>
      addressRepository.list().map{
        address => Ok(Json.toJson(address))
      }
    }
  }

  def delete_address(id: Long) = Action.async(
    addressRepository.delete(id).map(_ => Ok(""))
  )

  def edit_address(id: Long) = Action { Ok("edit user") }

}
case class CreateAddressForm(user_id: Long, country: String, city: String, street: String, home_number: Int, apartament_number: Int, postal_code: String)