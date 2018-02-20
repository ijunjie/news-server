package repos

import models.News
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocumentWriter, Macros}

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait NewsMongoRepo {
  import scala.concurrent.ExecutionContext.Implicits.global

  def mongoApi: ReactiveMongoApi

  def db: Future[DefaultDB] = mongoApi.connection.database("test")
  def newsCollection = db.map(_.collection("news"))

  implicit def newsWriter: BSONDocumentWriter[News] = Macros.writer[News]

  def createNews(news: News): Future[Unit] = {
    newsCollection.map(_.insert(news).map(_ => {}))
  }
}
