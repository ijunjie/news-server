package models

import models.targeting.{IntPredicate, IntRule, ReadTypeError, TargetingConfig}
import org.scalatest.{FunSuite, Matchers}

/**
  * @author Denis Pakhomov.
  * @version 1.0
  */
class IntRuleTest extends FunSuite with Matchers {

  val config: TargetingConfig = TargetingConfig(1, Map("level" -> classOf[Int], "country" -> classOf[String]))

  test("IntRule is invalid if key isn't exist") {
    val rule = new IntRule("moneySpent", 10, IntPredicate.EQ)
    assert(!rule.isValid(config))
  }

  test("IntRule is invalid if key isn't int type") {
    val rule = new IntRule("country", 5, IntPredicate.EQ)
    assert(!rule.isValid(config))
  }

  test("IntRule is valid if key exists and has int type") {
    val rule = new IntRule("level", 5, IntPredicate.EQ)
    assert(rule.isValid(config))
  }

  test("IntRule EQ predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.EQ)
    rule.test(Map("level" -> 5)) shouldBe Right(true)
    rule.test(Map("level" -> 4)) shouldBe Right(false)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }

  test("IntRule NEQ predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.NEQ)
    rule.test(Map("level" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> 4)) shouldBe Right(true)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }

  test("IntRule LT predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.LT)
    rule.test(Map("level" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> 6)) shouldBe Right(false)
    rule.test(Map("level" -> 4)) shouldBe Right(true)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }

  test("IntRule LE predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.LE)
    rule.test(Map("level" -> 5)) shouldBe Right(true)
    rule.test(Map("level" -> 6)) shouldBe Right(false)
    rule.test(Map("level" -> 4)) shouldBe Right(true)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }

  test("IntRule GT predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.GT)
    rule.test(Map("level" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> 6)) shouldBe Right(true)
    rule.test(Map("level" -> 4)) shouldBe Right(false)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }

  test("IntRule GE predicate cases") {
    val rule = new IntRule("level", 5, IntPredicate.GE)
    rule.test(Map("level" -> 5)) shouldBe Right(true)
    rule.test(Map("level" -> 6)) shouldBe Right(true)
    rule.test(Map("level" -> 4)) shouldBe Right(false)
    rule.test(Map("moneySpent" -> 5)) shouldBe Right(false)
    rule.test(Map("level" -> "5")) shouldBe Left(ReadTypeError)
  }
}
