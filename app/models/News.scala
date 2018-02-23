package models

import cats.data.Validated._
import cats.data.{NonEmptyList => NEL}
import cats.implicits._
import validation.ValidationConstraints._
import validation.{Field, ValidationError}

/**
  * @author Denis Pakhomov.
  */
case class News private(_id: String, title: String, body: String)

object News {

  def idValidated(id: String): ValidationResult[Field[String]] =
    fromOption(Option(Field(id, "id")), NEL.of(ValidationError("id", "id is missing")))
      .andThen(regexp(_, raw"^[a-z0-9_]+$$"))

  def titleValidated(title: String): ValidationResult[Field[String]] =
    fromOption(Option(Field(title, "title")), NEL.of(ValidationError("title", "title is missing")))
      .andThen(nonEmpty)
      .andThen(upperCased)

  def bodyValidated(body: String): ValidationResult[Field[String]] =
    fromOption(Option(Field(body, "body")), NEL.of(ValidationError("body", "body is missing")))
      .andThen(nonEmpty)

  def create(id: String, title: String, body: String): ValidationResult[News] = {
    (idValidated(id), titleValidated(title), bodyValidated(body)).mapN { case (vId, vTitle, vBody) =>
      News(vId.value, vTitle.value, vBody.value)
    }
  }
}
