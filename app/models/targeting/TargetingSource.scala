package models.targeting

import shapeless._

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait TargetingSource {
  def getAs[T: Typeable](key: String): Either[TargetingError, Option[T]]
  def get(key: String): Option[Any]
}

sealed trait TargetingError
case object ReadTypeError extends TargetingError

object TargetingSource {
  implicit class MapTargetingSource(map: Map[String, Any]) extends TargetingSource {
    override def getAs[T: Typeable](key: String): Either[TargetingError, Option[T]] = {
      val typeOf = TypeCase[T]
      map.get(key) match {
        case None => Right(None)
        case typeOf(value) => Right(Some(value))
        case _ => Left(ReadTypeError)
      }
    }

    override def get(key: String): Option[Any] = map.get(key)
  }
}