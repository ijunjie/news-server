package services

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import models.News
import repos.NewsRepo
import validation.ValidationConstraints.ValidationResult
import validation.ValidationError

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  */
trait NewsService {

  implicit def context: ExecutionContext
  def repo: NewsRepo

  def create(id: Option[String], title: Option[String], body: Option[String]): Future[ValidationResult[String]] = {
    News(id, title, body) match {
      case Valid(news: News) => repo.create(news).map(_ => Valid(news._id))
      case Invalid(errors: NonEmptyList[ValidationError]) => Future.successful(Invalid(errors))
    }
  }
}
