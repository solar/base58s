package org.sazabi.base58

import org.scalatest._

import scalaz._

class Base58Spec extends FlatSpec with Matchers with Inside {
  val valid = "17wjHPRxwP5QYu2CJsRqNP6gbre7Uig36N"
  val base58 = Base58String(valid)

  val bytes: Array[Byte] = Array(0, 76, 42, -125, 39, 65, 49, -98, -123,
    18, -47, 93, -73, -125, -84, -113, -65, 121, 90, 104, 69, -4, 8, 106,
    7)

  val invalid = "43290adfqOweoalalsdjfaqeowaur9324I"

  "Base58" should "convert bytearray into Base58String" in {
    println(Base58(bytes))
    Base58(bytes) shouldBe base58
  }

  it should "convert valid base58 String to Base58String" in {
    println(Base58.fromString(valid))
    Base58.fromString(valid) shouldBe \/-(base58)
  }

  it should "return exception if received an invalid string" in {
    inside(Base58.fromString(invalid)) {
      case -\/(e) => println(e) 
    }
  }

  it should "convert Base58String to Array[Byte]" in {
    Base58.toByteArray(base58).map(_.toSeq) shouldBe \/-(bytes.toSeq)
  }
}
