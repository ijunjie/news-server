package models

import scala.reflect.ClassTag

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait TargetingPredicate {
  def test(profile: TargetingSource): Either[TargetingError, Boolean]
}

abstract class CompoundPredicate(val predicates: Vector[TargetingPredicate], combine: (Boolean, Boolean) => Boolean,
                                 start: Boolean) extends TargetingPredicate {
  override def test(profile: TargetingSource): Either[TargetingError, Boolean] =
    predicates.map(_.test(profile)).foldLeft(Right(start): Either[TargetingError, Boolean])(
    (either1, either2) => either1.right.flatMap(result => either2.right.map(combine(result, _))))
}

class AndPredicate(predicates: Vector[TargetingPredicate]) extends CompoundPredicate(predicates, (pr1, pr2) => pr1 && pr2, start = true)
class OrPredicate(predicates: Vector[TargetingPredicate]) extends CompoundPredicate(predicates, (pr1, pr2) => pr1 || pr2, start = false)


abstract class ParamPredicate[T: ClassTag](val param: T, val key: String, compare: (T, T) => Boolean) extends TargetingPredicate {
  override def test(profile: TargetingSource): Either[TargetingError, Boolean] =
    profile.getAs[T](key).right.map(paramValue => paramValue.exists(compare(param, _)))
}

class EqPredicate[T: ClassTag](param: T, key: String) extends ParamPredicate(param, key, (p: T, v: T) => p.equals(v))

class IntPredicate(param: Int, key: String, compare: (Int, Int) => Boolean) extends ParamPredicate(param, key, compare)

class GtPredicate(param: Int, key: String) extends IntPredicate(param, key, (p, v) => p > v)
class GePredicate(param: Int, key: String) extends IntPredicate(param, key, (p, v) => p >= v)

class LtPredicate(param: Int, key: String) extends IntPredicate(param, key, (p, v) => p < v)
class LePredicate(param: Int, key: String) extends IntPredicate(param, key, (p, v) => p <= v)