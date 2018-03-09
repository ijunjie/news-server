package controllers

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import models.News
import play.api.data.Form
import play.api.mvc.{AbstractController, ControllerComponents}
import services.NewsService
import validation.{BusinessLogicError, ServerError, ValidationError}

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class NewsController(newsService: NewsService, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with play.api.i18n.I18nSupport {

  import forms.NewsForm._

  private val postUrl = routes.NewsController.addSave()

  private val updateUrl = routes.NewsController.editSave()

  def addForm = Action { implicit request =>
    Ok(views.html.news(form, postUrl, update = false))
  }

  def addSave = Action.async { implicit request =>

    val resultForm = form.bindFromRequest

    resultForm.fold(
      formWithErrors => Future.successful(BadRequest(views.html.news(formWithErrors, postUrl, update = false))),

      newsData => newsService.create(newsData.id, newsData.title, newsData.body).map {
        case Valid(_) => Redirect(routes.HomeController.index())
        case Invalid(errors: NonEmptyList[ServerError]) =>
          BadRequest(views.html.news(formWithErrors(resultForm, errors.toList), postUrl, update = false))
      }
    )
  }

  def editForm(id: String) = Action.async { implicit request =>
    newsService.findById(id).map {
      case Some(Valid(news: News)) => Ok(views.html.news(
        form.fill(NewsData(Some(news._id), Some(news.title), Some(news.body))), updateUrl, update = true))
      case Some(Invalid(_)) => InternalServerError(views.html.error("error deserialize news"))
      case None => NotFound(views.html.error(s"news with id $id is not found"))
    }
  }

  def editSave() = Action.async { implicit request =>

    val updateForm = form.bindFromRequest

    updateForm.fold(

      formWithErrors => Future.successful(BadRequest(views.html.news(formWithErrors, updateUrl, update = true))),

      newsData => newsService.update(newsData.id, newsData.title, newsData.body).map {
        case Valid(_) => Redirect(routes.HomeController.index())
        case Invalid(errors: NonEmptyList[ServerError]) =>
          BadRequest(views.html.news(formWithErrors(updateForm, errors.toList), updateUrl, update = true))
      }

    )
  }

  protected def formWithErrors[T](form: Form[T], errors: Iterable[ServerError]): Form[T] =
    errors.foldLeft(form)((foldForm, error) => error match {
      case ValidationError(field, message) => foldForm.withError(field, message)
      case error: ServerError => foldForm.withGlobalError(error.message)
    })
}
