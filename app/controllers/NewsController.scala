package controllers

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.NewsService
import validation.ValidationError

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsController(newsService: NewsService, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with play.api.i18n.I18nSupport {

  import forms.NewsForm._

  private val postUrl = routes.NewsController.newsPost()

  def news = Action { implicit request =>
    Ok(views.html.news(form, postUrl))
  }

  def newsPost = Action.async { implicit request =>

    val resultForm = form.bindFromRequest

    resultForm.fold(
      formWithErrors => Future.successful(BadRequest(views.html.news(formWithErrors, postUrl))),

      newsData => newsService.create(newsData.id, newsData.title, newsData.body).map {
        case Valid(_) => Redirect(routes.HomeController.index())
        case Invalid(errors: NonEmptyList[ValidationError]) =>
          val errorForm = errors.foldLeft(resultForm)((foldForm, error) => foldForm.withError(error.field, error.errorMessage))
          BadRequest(views.html.news(errorForm, postUrl))
      }
    )
  }
}
