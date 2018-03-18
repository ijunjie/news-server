package models

import scala.reflect.ClassTag

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait TargetingSource {
  def getAs[T](key: String)(implicit tag: ClassTag[T]): Either[TargetingError, Option[T]]
}

sealed trait TargetingError
case object ReadTypeError extends TargetingError

object TargetingSource {
  implicit class MapTargetingSource(map: Map[String, AnyVal]) extends TargetingSource {
    override def getAs[T](key: String)(implicit tag: ClassTag[T]): Either[TargetingError, Option[T]] = map.get(key) match {
      case None => Right(None)
      case Some(value: T) => Right(Some(value))
      case _ => Left(ReadTypeError)
    }
  }
}