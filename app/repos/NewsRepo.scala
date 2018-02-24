package repos

import models.News

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait NewsRepo {

  def create(news: News): Future[Unit]

  def findById(id: String): Future[Option[News]]
}
