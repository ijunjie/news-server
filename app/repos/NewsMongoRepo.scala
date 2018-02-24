package repos

import models.News
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait NewsMongoRepo extends NewsRepo {

  implicit def context: ExecutionContext

  def mongoApi: ReactiveMongoApi

  def db: Future[DefaultDB] = mongoApi.connection.database("test")
  def newsCollection: Future[BSONCollection] = db.map(_.collection("news"))

  implicit def newsWriter: BSONDocumentWriter[News] = Macros.writer[News]
  implicit def newsReader: BSONDocumentReader[News] = Macros.reader[News]

  override def create(news: News): Future[Unit] = {
    newsCollection.map(_.insert(news).map(_ => {}))
  }

  override def findById(id: String): Future[Option[News]] = {
    newsCollection.flatMap(_.find(BSONDocument("_id" -> id)).one[News])
  }
}
