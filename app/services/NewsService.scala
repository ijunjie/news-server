package services

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import dto.NewsListDto
import models.News
import repos.NewsRepo
import validation.ValidationConstraints.ValidationResult
import validation.{BusinessLogicError, EntityNotFoundError, ServerError}

import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  */
trait NewsService {

  protected val tryLimit: Int = 5

  implicit def context: ExecutionContext
  def repo: NewsRepo

  def create(id: Option[String], title: Option[String], body: Option[String]): Future[ValidationResult[String]] = {
    News.create(id, title, body) match {
      case Valid(news: News) => repo.create(news).map(Valid(_))
      case Invalid(errors: NonEmptyList[ServerError]) => successful(Invalid(errors))
    }
  }

  def update(id: Option[String], title: Option[String], body: Option[String]): Future[ValidationResult[String]] =
    update(id, title, body, tryCount = 0)

  def update(id: Option[String], title: Option[String], body: Option[String], tryCount: Int): Future[ValidationResult[String]] = {
    id match {
      case Some(idVal) => findById(idVal).flatMap {
        case Some(Invalid(_)) => successful(Invalid(NonEmptyList.one(BusinessLogicError(s"news with id $id is corrupted"))))
        case Some(Valid(news)) => News(id, Some(news.version), title, body) match {
          case Valid(next: News) => repo.update(next).flatMap {
            case Right(newsId: String) => Future.successful(Valid(newsId))
            case Left(error: EntityNotFoundError) =>
              if (tryCount < tryLimit) update(id, title, body, tryCount + 1)
              else Future.successful(Invalid(NonEmptyList.one(error)))
            case Left(error: ServerError) => Future.successful(Invalid(NonEmptyList.one(error)))
          }
          case p @ Invalid(_) => successful(p)
        }
        case None => successful(Invalid(NonEmptyList.one(BusinessLogicError("news doesn't exist"))))
      }
      case None => successful(Invalid(NonEmptyList.of(BusinessLogicError("id is missing"))))
    }
  }

  def findById(id: String): Future[Option[ValidationResult[News]]] = repo.findById(id)

  def newsList(limit: Int = -1): Future[Vector[NewsListDto]] = repo.newsList(limit)
}
