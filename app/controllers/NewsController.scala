package controllers

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import models.{ValidationError, News}
import play.api.mvc.{AbstractController, ControllerComponents}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsController(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  import forms.NewsForm._

  private val postUrl = routes.NewsController.newsPost()

  def news = Action { implicit request =>
    Ok(views.html.news(form, postUrl))
  }

  def newsPost = Action { implicit request =>

    val resultForm = form.bindFromRequest

    resultForm.fold(
      formWithErrors => BadRequest(views.html.news(formWithErrors, postUrl)),
      newsData => News.create(newsData.title, newsData.body) match {
        case Valid(_) => Redirect(routes.HomeController.index())
        case Invalid(errors: NonEmptyList[ValidationError]) =>
          val errorForm = errors.foldLeft(resultForm)((foldForm, error) => foldForm.withError(error.field, error.errorMessage))
          BadRequest(views.html.news(errorForm, postUrl))
      }
    )
  }
}
