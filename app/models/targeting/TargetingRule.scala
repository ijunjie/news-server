package models.targeting

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
trait TargetingRule {
  def test(profile: Map[String, Any]): Either[TargetingError, Boolean]
  def isValid(config: TargetingConfig): Boolean
}