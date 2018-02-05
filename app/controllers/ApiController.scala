package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Future

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class ApiController (cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action.async {
    Future.successful(Ok(Json.obj(
      "message" -> "ok"
    )).as(JSON))
  }
}
