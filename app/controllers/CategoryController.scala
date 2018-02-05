package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import repos.MongoCategoryRepo

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class CategoryController(cc: ControllerComponents, categoryRepo: MongoCategoryRepo) extends AbstractController(cc) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def createCategory = Action.async {
    categoryRepo.createCategory()
      .map(_ => Ok(Json.obj("message" -> "ok")).as(JSON))
  }
}
