package controllers

import javax.inject._
import models.ProductRepository
import play.api.data.Form
import play.api.data.Forms.{ mapping, _ }
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.format.Formats._

import scala.concurrent.{ ExecutionContext, Future }

/**
 */
@Singleton
class ProductController @Inject() (productRepository: ProductRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "category_id" -> of(longFormat),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "country_of_origin" -> nonEmptyText,
      "weight" -> number,
      "price" -> of(doubleFormat)
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  def add_product = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create product."))
      },
      product => {
        productRepository.create(
          product.category_id,
          product.name,
          product.description,
          product.country_of_origin,
          product.weight,
          product.price
        ).map { product =>
            Created(Json.toJson(product))
          }
      }
    )
  }

  def get_product(id: Long) = Action.async { implicit request =>
    val options = for {
      maybeProduct <- productRepository.findById(id)
    } yield (maybeProduct)

    options.map {
      case (opt) =>
        opt match {
          case Some(product) => Ok(Json.toJson(product))
          case None => NotFound
        }
    }
  }

  def get_products = {
    Action.async { implicit request =>
      productRepository.list().map {
        product => Ok(Json.toJson(product))
      }
    }
  }

  def delete_product(id: Long) = Action.async(
    productRepository.delete(id).map(_ => Ok(""))
  )

  def edit_product(id: Long) = Action { Ok("edit user") }

}
case class CreateProductForm(category_id: Long, name: String, description: String, country_of_origin: String, weight: Int, price: Double)