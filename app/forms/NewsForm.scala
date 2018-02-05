package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
object NewsForm {
  case class NewsData(title: String, body: String)

  val form = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText
    )(NewsData.apply)(NewsData.unapply)
  )
}
