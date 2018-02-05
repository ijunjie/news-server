package controllers

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
    Redirect(routes.HomeController.index())
  }
}
