package repos

import dto.NewsListDto
import models.News
import validation.ServerError
import validation.ValidationConstraints.ValidationResult

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait NewsRepo {

  def create(news: News): Future[String]

  def update(news: News): Future[Either[ServerError, String]]

  def findById(id: String): Future[Option[ValidationResult[News]]]

  def newsList(limit: Int = -1): Future[Vector[NewsListDto]]
}
