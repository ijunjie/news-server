package models

import models.targeting.{BooleanPredicate, BooleanRule, ReadTypeError, TargetingConfig}
import org.scalatest.{FunSuite, Matchers}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class BooleanRuleTest extends FunSuite with Matchers {

  val config: TargetingConfig = TargetingConfig(1, Map("admin" -> classOf[Boolean], "country" -> classOf[String]))

  test("BooleanRule is invalid if field isn't exist") {
    val rule = new BooleanRule("newcomer", true, BooleanPredicate.EQ)
    rule.isValid(config) shouldBe false
  }

  test("BooleanRule is invalid if field has invalid type") {
    val rule = new BooleanRule("country", true, BooleanPredicate.EQ)
    rule.isValid(config) shouldBe false
  }

  test("BooleanRule is valid if field has boolean type") {
    val rule = new BooleanRule("admin", true, BooleanPredicate.EQ)
    rule.isValid(config) shouldBe true
  }

  test("BooleanRule EQ predicate cases") {
    val rule = new BooleanRule("admin", true, BooleanPredicate.EQ)
    rule.test(Map("admin" -> true)) shouldBe Right(true)
    rule.test(Map("admin" -> false)) shouldBe Right(false)
    rule.test(Map.empty[String, Any]) shouldBe Right(false)
    rule.test(Map("admin" -> "yes")) shouldBe Left(ReadTypeError)
  }
}
