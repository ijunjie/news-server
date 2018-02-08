package models

import cats.data.{NonEmptyList => NEL}

import cats.data.ValidatedNel
import cats.data.Validated._
import cats.implicits._

/**
  * @author Denis Pakhomov.
  */
case class News private(title: String, body: String)

case class ValidationError(field: String, errorMessage: String)

object News {

  type ValidationResult[R] = ValidatedNel[ValidationError, R]

  def validateNotEmptyString(str: String, fieldName: String): ValidationResult[String] =
    condNel(!str.isEmpty, str, ValidationError(fieldName, s"$fieldName is empty"))

  def validateUpperCase(str: String, fieldName: String): ValidatedNel[ValidationError, String] =
    condNel(str.equals(str.toUpperCase()), str, ValidationError(fieldName,
      s"$fieldName isn't in lower case"))



  def validatedTitle(title: String): ValidationResult[String] =
    fromOption(Option(title), NEL.of(ValidationError("title", "title is missing")))
      .andThen(validateNotEmptyString(_, "title"))
      .andThen(validateUpperCase(_, "title"))

  def validatedBody(body: String): ValidationResult[String] =
    fromOption(Option(body), NEL.of(ValidationError("body", "body is missing")))
      .andThen(validateNotEmptyString(_, "body"))

  def create(title: String, body: String): ValidationResult[News] = {

    (validatedTitle(title), validatedBody(body)).mapN { case (vTitle: String, vBody: String) => News(vTitle, vBody) }
  }
}
