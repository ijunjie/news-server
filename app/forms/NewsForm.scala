package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
object NewsForm {
  case class NewsData(id: Option[String], title: Option[String], body: Option[String])

  val form = Form(
    mapping(
      "id" -> optional(text),
      "title" -> optional(text),
      "body" -> optional(text)
    )(NewsData.apply)(NewsData.unapply)
  )
}
