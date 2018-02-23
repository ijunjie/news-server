package models

import cats.data.Validated._
import cats.data.{NonEmptyList => NEL}
import cats.implicits._
import validation.ValidationConstraints._
import validation.{Field, ValidationError}

/**
  * @author Denis Pakhomov.
  */
case class News private(title: String, body: String)

object News {

  def validatedTitle(title: String): ValidationResult[Field[String]] =
    fromOption(Option(Field(title, "title")), NEL.of(ValidationError("title", "title is missing")))
      .andThen(nonEmpty)
      .andThen(upperCased)

  def validatedBody(body: String): ValidationResult[Field[String]] =
    fromOption(Option(Field(body, "body")), NEL.of(ValidationError("body", "body is missing")))
      .andThen(nonEmpty)

  def create(title: String, body: String): ValidationResult[News] = {
    (validatedTitle(title), validatedBody(body)).mapN { case (vTitle, vBody) => News(vTitle.value, vBody.value) }
  }
}
