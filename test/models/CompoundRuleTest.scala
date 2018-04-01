package models

import models.targeting.{RuleCompose, _}
import org.scalatest.{FunSuite, Matchers}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class CompoundRuleTest extends FunSuite with Matchers {

  val config: TargetingConfig = TargetingConfig(version = 1, Map("level" -> classOf[Int], "country" -> classOf[String]))

  test("CompoundRule is invalid if any rule is invalid") {
    val rule = new CompoundRule(
      Vector(new IntRule("level", 5, IntPredicate.EQ), new StringRule("username", "admin", StringPredicate.EQ)),
      RuleCompose.AND)

    rule.isValid(config) shouldBe false
  }

  test("CompoundRule is valid if all rules are valid") {
    val rule = new CompoundRule(
      Vector(new IntRule("level", 5, IntPredicate.EQ), new StringRule("country", "RU", StringPredicate.EQ)),
      RuleCompose.AND)

    rule.isValid(config) shouldBe true
  }

  test("CompoundRule AND cases") {
    val rule = new CompoundRule(
      Vector(new IntRule("level", 3, IntPredicate.GE), new IntRule("level", 5, IntPredicate.LE)),
      RuleCompose.AND
    )

    rule.test(Map("level" -> 4)) shouldBe Right(true)
    rule.test(Map("level" -> 6)) shouldBe Right(false)
    rule.test(Map.empty[String, Any]) shouldBe Right(false)
    rule.test(Map("level" -> "4")) shouldBe Left(ReadTypeError)
  }

  test("CompoundRule OR cases") {
    val rule = new CompoundRule(
      Vector(new IntRule("level", 3, IntPredicate.LE), new IntRule("level", 5, IntPredicate.GE)),
      RuleCompose.OR
    )

    rule.test(Map("level" -> 6)) shouldBe Right(true)
    rule.test(Map("level" -> 4)) shouldBe Right(false)
    rule.test(Map.empty[String, Any]) shouldBe Right(false)
    rule.test(Map("level" -> "4")) shouldBe Left(ReadTypeError)
  }
}
