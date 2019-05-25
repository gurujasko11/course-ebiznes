package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class ProductRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */

  private class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

    /** The ID column, which is the primary key, and auto incremented */
    def product_id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def category_id = column[Long]("category_id")
    /** The name column */
    def name = column[String]("name")

    /** The age column */
    def description = column[String]("description")

    def country_of_origin = column[String]("country_of_origin")

    def weight = column[Int]("weight")

    def price = column[Double]("price")

    def category_fk = foreignKey("cat_fk",category_id, cat)(_.id)


    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (product_id, category_id, name, description, country_of_origin, weight, price) <> ((Product.apply _).tupled, Product.unapply)
    //def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */

  import categoryRepository.CategoryTable

  private val product = TableQuery[ProductTable]

  private val cat = TableQuery[CategoryTable]


  /**
   * Create a person with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(category_id: Long, name: String, description: String, country_of_origin: String, weight: Int, price: Int): Future[Product] = db.run {
    (product.map(p => (p.category_id, p.name, p.description, p.country_of_origin, p.weight, p.price))
      returning product.map(_.product_id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into {case ((category_id, name, description, country_of_origin, weight, price),id) => Product(id, category_id, name, description, country_of_origin, weight, price)}
    // And finally, insert the person into the database
    ) += (category_id, name, description, country_of_origin, weight, price)
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getByCategory(category_id: Long): Future[Seq[Product]] = db.run {
    product.filter(_.category_id === category_id).result
  }

  def getByCategories(category_ids: List[Long]): Future[Seq[Product]] = db.run {
    product.filter(_.category_id inSet category_ids).result
  }


}
