package repos

import loaders.ReactiveMongoClient
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Collection, DefaultDB}
import reactivemongo.bson.{BSONDocumentWriter, Macros}

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait MongoCategoryRepo {

  import scala.concurrent.ExecutionContext.Implicits.global

  def mongoApi: ReactiveMongoApi

  def db: Future[DefaultDB] = mongoApi.connection.database("test")
  def categoriesCollection = db.map(_.collection("categories2"))

  implicit def categoryWriter: BSONDocumentWriter[Category] = Macros.writer[Category]

  def createCategory() = {

    categoriesCollection.map(_.insert(Category("test")).map(_ => {}))
  }

//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  val futureConnection = Future.fromTry(connection)
//
//  def db1: Future[DefaultDB] = futureConnection.flatMap(_.database("test"))
//  def categoryCollection = db1.map(_.collection("categories"))
//
//  implicit def categoryWriter: BSONDocumentWriter[Category] = Macros.writer[Category]
//
//  def createCategory(): Future[Unit] = {
//    categoryCollection.flatMap(_.insert(Category("test")).map(_ => {}))
//  }
}

case class Category(name: String)
