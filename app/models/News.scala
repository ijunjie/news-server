package models

import cats.data.Validated._
import cats.data.{NonEmptyList => NEL}
import cats.implicits._
import validation.ValidationConstraints._
import validation.{Field, ValidationError}

/**
  * @author Denis Pakhomov.
  */
case class News private(_id: String, version: Int, title: String, body: String)

object News {

  def idValidated(id: Option[String]): ValidationResult[Field[String]] =
    fromOption(id, NEL.of(ValidationError("id", "id is missing")))
        .map(value => Field(value, "id"))
      .andThen(regexp(_, raw"^[a-z0-9_]+$$"))

  def versionValidated(version: Option[Int]): ValidationResult[Field[Int]] =
    fromOption(version, NEL.of(ValidationError("version", "version is missing")))
      .map(value => Field(value, "version"))
    .andThen(nonNegative)

  def titleValidated(title: Option[String]): ValidationResult[Field[String]] =
    fromOption(title, NEL.of(ValidationError("title", "title is missing")))
        .map(value => Field(value, "title"))
      .andThen(nonEmpty)
      .andThen(upperCased)

  def bodyValidated(body: Option[String]): ValidationResult[Field[String]] =
    fromOption(body, NEL.of(ValidationError("body", "body is missing")))
        .map(value => Field(value, "body"))
      .andThen(nonEmpty)

  def create(id: Option[String], title: Option[String], body: Option[String]): ValidationResult[News] =
    apply(id, Some(1), title, body)

  def apply(id: Option[String], version: Option[Int], title: Option[String], body: Option[String]): ValidationResult[News] = {
    (idValidated(id), versionValidated(version), titleValidated(title), bodyValidated(body)).mapN {
      case (idField, versionField, titleField, bodyField) =>
        new News(idField.value, versionField.value, titleField.value, bodyField.value)
    }
  }
}
