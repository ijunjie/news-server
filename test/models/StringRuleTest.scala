package models

import models.targeting.{ReadTypeError, StringPredicate, StringRule, TargetingConfig}
import org.scalatest.{FunSuite, Matchers}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class StringRuleTest extends FunSuite with Matchers {

  val config: TargetingConfig = TargetingConfig(1, Map("country" -> classOf[String], "level" -> classOf[Int]))

  test("StringRule is invalid if field isn't exist") {
    val rule = new StringRule("username", "test", StringPredicate.EQ)
    rule.isValid(config) shouldBe false
  }

  test("StringRule is invalid if field has invalid type") {
    val rule = new StringRule("level", "test", StringPredicate.EQ)
    rule.isValid(config) shouldBe false
  }

  test("StringRule is valid if field has valid type") {
    val rule = new StringRule("country", "RU", StringPredicate.EQ)
    rule.isValid(config) shouldBe true
  }

  test("StringRule EQ predicate cases") {
    val rule = new StringRule("country", "RU", StringPredicate.EQ)
    rule.test(Map("country" -> "RU")) shouldBe Right(true)
    rule.test(Map("country" -> "US")) shouldBe Right(false)
    rule.test(Map.empty[String, Any]) shouldBe Right(false)
    rule.test(Map("country" -> 5)) shouldBe Left(ReadTypeError)
  }

  test("StringRule NEQ predicate cases") {
    val rule = new StringRule("country", "RU", StringPredicate.NEQ)
    rule.test(Map("country" -> "RU")) shouldBe Right(false)
    rule.test(Map("country" -> "US")) shouldBe Right(true)
    rule.test(Map.empty[String, Any]) shouldBe Right(false)
    rule.test(Map("country" -> 5)) shouldBe Left(ReadTypeError)
  }
}
