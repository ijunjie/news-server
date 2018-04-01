package models.targeting

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class CompoundRule(val predicates: Vector[TargetingRule], compose: RuleCompose) extends TargetingRule {

  override def test(profile: Map[String, Any]): Either[TargetingError, Boolean] =
    predicates.map(_.test(profile)).foldLeft(Right(compose.init): Either[TargetingError, Boolean])(
      (either1, either2) => either1.right.flatMap(result => either2.right.map(compose.func(result, _))))

  override def isValid(config: TargetingConfig): Boolean = predicates.forall(_.isValid(config))
}

sealed trait RuleCompose {
  val func: (Boolean, Boolean) => Boolean
  val init: Boolean
}

object RuleCompose {
  case object AND extends RuleCompose {
    val func: (Boolean, Boolean) => Boolean = (r1, r2) => r1 && r2
    val init: Boolean = true
  }

  case object OR extends RuleCompose {
    val func: (Boolean, Boolean) => Boolean = (r1, r2) => r1 || r2
    val init: Boolean = false
  }
}