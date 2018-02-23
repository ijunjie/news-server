package controllers

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import models.News
import play.api.mvc.{AbstractController, ControllerComponents}
import repos.NewsMongoRepo
import validation.ValidationError

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsController(newsRepo: NewsMongoRepo, cc: ControllerComponents) extends AbstractController(cc)
  with play.api.i18n.I18nSupport {

  import scala.concurrent.ExecutionContext.Implicits.global

  import forms.NewsForm._

  private val postUrl = routes.NewsController.newsPost()

  def news = Action { implicit request =>
    Ok(views.html.news(form, postUrl))
  }

  def newsPost = Action.async { implicit request =>

    val resultForm = form.bindFromRequest

    resultForm.fold(
      formWithErrors => Future.successful(BadRequest(views.html.news(formWithErrors, postUrl))),
      newsData => News.create(newsData.id, newsData.title, newsData.body) match {
        case Valid(news: News) => newsRepo.createNews(news).map(_ => Redirect(routes.HomeController.index()))
        case Invalid(errors: NonEmptyList[ValidationError]) =>
          val errorForm = errors.foldLeft(resultForm)((foldForm, error) => foldForm.withError(error.field, error.errorMessage))
          Future.successful(BadRequest(views.html.news(errorForm, postUrl)))
      }
    )
  }
}
