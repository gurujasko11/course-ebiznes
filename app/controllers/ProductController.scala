package controllers

import javax.inject._
import play.api.mvc._
import models.ProductRepository
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

/**
  */
@Singleton
class ProductController @Inject()(productRepository: ProductRepository,cc: ControllerComponents) (implicit ec: ExecutionContext )extends AbstractController(cc) {

  def get_all = Action.async(
    implicit request => (
      productRepository.list().map(
        product => Ok(Json.toJson(product))
      )
      )
  )
  def add_product = Action { Ok("add product") }
  def get_product(id: String) = Action { Ok("get product") }
  def get_products = Action { Ok("get products")}
  def delete_product(id: String) = Action { Ok("delete product") }
  def edit_product(id: String) = Action { Ok("edit product") }
  def find_products = Action { Ok("find products") }
}