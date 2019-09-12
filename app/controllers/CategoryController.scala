package controllers

import javax.inject._
import models.CategoryRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class CategoryController @Inject() (categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  def get_categories = Action.async { implicit request =>
    categoryRepo.list().map { category =>
      Ok(Json.toJson(category)).withHeaders(
        "Access-Control-Allow-Origin" -> "*")
    }
  }

  def get_category_by_id(id: Int) = Action.async { implicit request =>
    val options = for {
      maybeCategory <- categoryRepo.findById(id)
    } yield (maybeCategory)

    options.map {
      case (opt) =>
        opt match {
          case Some(category) => Ok(Json.toJson(category))
          case None => NotFound
        }
    }
  }

  def create() = Action.async(parse.json) { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest("failed to create category"))
      },
      category => {

        categoryRepo.create(
          category.name: String
        ).map { _ =>
            Ok("succesfully added new category").withHeaders(
              "Access-Control-Allow-Origin" -> "*")
          }
      }
    )
  }

  def delete(id: Int) = Action {
    categoryRepo.delete(id)
    Ok("Successfully removed").withHeaders(
      "Access-Control-Allow-Origin" -> "*")
  }
  def edit_category(id: Int) =
    Action.async(parse.json) {
      implicit request =>
        categoryForm.bindFromRequest.fold(
          _ => {
            Future.successful(BadRequest("failed to update category."))
          },
          category => {
            categoryRepo.update(models.Category(
              id,
              category.name
            )).map({ _ =>
              Ok
            })
          }
        )
    }

}
case class CreateCategoryForm(name: String)