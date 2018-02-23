package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
object NewsForm {
  case class NewsData(id: String, title: String, body: String)

  val form = Form(
    mapping(
      "id" -> text,
      "title" -> text,
      "body" -> text
    )(NewsData.apply)(NewsData.unapply)
  )
}
