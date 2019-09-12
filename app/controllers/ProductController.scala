package controllers

import javax.inject._
import models.ProductRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
/**
 */
@Singleton
class ProductController @Inject() (productRepository: ProductRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "category_id" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "country_of_origin" -> nonEmptyText,
      "weight" -> number,
      "price" -> number
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  def add_product = Action { implicit request =>
    val name = request.body.asJson.get("name").as[String]
    val description = request.body.asJson.get("description").as[String]
    val category = request.body.asJson.get("category_id").as[Int]
    val country_of_origin = request.body.asJson.get("country_of_origin").as[String]
    val weight = request.body.asJson.get("weight").as[Int]
    val price = request.body.asJson.get("price").as[Int]

    productRepository.create(category, name, description, country_of_origin, weight, price)
    Ok("Added product").withHeaders(
      "Access-Control-Allow-Origin" -> "*")
  }

  def update_product(id: Int) = Action.async { implicit request =>
    val name = request.body.asJson.get("name").as[String]
    val description = request.body.asJson.get("description").as[String]
    val category = request.body.asJson.get("category_id").as[Int]
    val country_of_origin = request.body.asJson.get("country_of_origin").as[String]
    val weight = request.body.asJson.get("weight").as[Int]
    val price = request.body.asJson.get("price").as[Int]

    productRepository.update(id, category, name, description, country_of_origin, weight, price).map { product =>
      Ok(Json.toJson(product)).withHeaders(
        "Access-Control-Allow-Origin" -> "*")
    }
  }

  def get_product(id: Int) = Action.async { implicit request =>
    productRepository.findById(id).map { product =>
      Ok(Json.toJson(product)).withHeaders(
        "Access-Control-Allow-Origin" -> "*")
    }
  }

  def get_products = {
    Action.async { implicit request =>
      productRepository.list().map {
        product =>
          Ok(Json.toJson(product)).withHeaders(
            "Access-Control-Allow-Origin" -> "*")
      }
    }
  }

  def delete_product(id: Int) = Action.async(
    productRepository.delete(id).map(_ => Ok(""))
  )

}
case class CreateProductForm(category_id: Int, name: String, description: String, country_of_origin: String, weight: Int, price: Int)