package repos

import models.News
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, Macros}
import validation.ValidationConstraints.ValidationResult

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait NewsMongoRepo extends NewsRepo with BSONReaders{

  implicit def context: ExecutionContext

  def mongoApi: ReactiveMongoApi

  def db: Future[DefaultDB] = mongoApi.connection.database("test")
  def newsCollection: Future[BSONCollection] = db.map(_.collection("news"))

  implicit def newsWriter: BSONDocumentWriter[News] = Macros.writer[News]

  override def create(news: News): Future[String] = {
    newsCollection.flatMap(_.insert(news)).map(_ => news._id)
  }

  override def update(news: News): Future[String] = {
    val selector = BSONDocument("_id" -> news._id, "version" -> news.version)
    val setModifier = BSONDocument(
      "$set" -> BSONDocument("title" -> news.title, "body" -> news.body),
      "$inc" -> BSONDocument("version" -> 1)
    )

    newsCollection.flatMap(_.update(selector, setModifier)).map(_ => news._id)
  }
  override def findById(id: String): Future[Option[ValidationResult[News]]] = {
    newsCollection.flatMap(_.find(BSONDocument("_id" -> id)).one[ValidationResult[News]])
  }
}
