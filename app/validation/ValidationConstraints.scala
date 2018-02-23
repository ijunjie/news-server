package validation

import cats.data.Validated.condNel
import cats.data.ValidatedNel

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
object ValidationConstraints {

  type ValidationResult[R] = ValidatedNel[ValidationError, R]

  def nonEmpty(field: Field[String]): ValidationResult[Field[String]] =
    condNel(!field.value.isEmpty, field, ValidationError(field.name, s"${field.name} is empty"))

  def upperCased(field: Field[String]): ValidationResult[Field[String]] =
    condNel(!field.value.isEmpty, field, ValidationError(field.name, s"${field.name} isn't upper cased"))

  def regexp(field: Field[String], regex: String) =
    condNel(regex.r.findAllIn(field.value).nonEmpty, field,
      ValidationError(field.name, s"${field.name} doesn't match pattern"))
}

case class ValidationError(field: String, errorMessage: String)
case class Field[T](value: T, name: String)
