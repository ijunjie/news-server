package models.targeting

import shapeless.TypeCase

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
abstract class ParamRule[T](val key: String, val param: T, compare: Predicate[T]) extends TargetingRule {
}

class IntRule(key: String, param: Int, compare: IntPredicate) extends ParamRule(key, param, compare) {

  private val intTypeCase = TypeCase[Int]

  override def isValid(config: TargetingConfig): Boolean = config.fields.get(key).contains(classOf[Int])

  override def test(profile: Map[String, Any]): Either[TargetingError, Boolean] =
    profile.get(key).map {
      case intTypeCase(value) => Right(compare.func(param, value))
      case _ => Left(ReadTypeError)
    }.getOrElse(Right(false))
}

class StringRule(key: String, param: String, compare: StringPredicate) extends ParamRule(key, param, compare) {

  private val stringTypeCase = TypeCase[String]

  override def isValid(config: TargetingConfig): Boolean = config.fields.get(key).contains(classOf[String])

  override def test(profile: Map[String, Any]): Either[TargetingError, Boolean] =
    profile.get(key).map {
      case stringTypeCase(value) => Right(compare.func(param, value))
      case _ => Left(ReadTypeError)
    }.getOrElse(Right(false))
}

class BooleanRule(key: String, param: Boolean, compare: BooleanPredicate) extends ParamRule(key, param, compare) {

  private val booleanTypeCase = TypeCase[Boolean]

  override def isValid(config: TargetingConfig): Boolean = config.fields.get(key).contains(classOf[Boolean])

  override def test(profile: Map[String, Any]): Either[TargetingError, Boolean] =
    profile.get(key).map {
      case booleanTypeCase(value) => Right(compare.func(param, value))
      case _ => Left(ReadTypeError)
    }.getOrElse(Right(false))
}

sealed trait Predicate[T] { val func: (T, T) => Boolean }

sealed trait IntPredicate extends Predicate[Int]

object IntPredicate {
  case object EQ  extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => p.equals(v) }
  case object NEQ extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => !p.equals(v) }
  case object LT  extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => v < p }
  case object LE  extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => v <= p }
  case object GT  extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => v > p }
  case object GE  extends IntPredicate { val func: (Int, Int) => Boolean = (p: Int, v: Int) => v >= p }
}



sealed trait StringPredicate extends Predicate[String]

object StringPredicate {
  case object EQ  extends StringPredicate { val func: (String, String) => Boolean = (p: String, v: String) => p.equals(v) }
  case object NEQ extends StringPredicate { val func: (String, String) => Boolean = (p: String, v: String) => !p.equals(v) }
}



sealed trait BooleanPredicate extends Predicate[Boolean]

object BooleanPredicate {
  case object EQ  extends BooleanPredicate { val func: (Boolean, Boolean) => Boolean = (p: Boolean, v: Boolean) => p.equals(v) }
}