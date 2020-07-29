package org.sazabi.base58

import scala.util.Success

import scalaprops._, Property.forAll

object Base58Test extends Scalaprops {
  private[this] val invalidStr = for {
    n <- Gen.choose(1, 12)
    chars <- Gen.sequenceNList(n, Gen.elements('0', 'O', 'l', 'I', '/', '+'))
  } yield chars.mkString

  private[this] val valid = "17wjHPRxwP5QYu2CJsRqNP6gbre7Uig36N"
  private[this] val base58 = Base58String(valid)

  val p1 = {
    val apply = forAll { (bytes: Array[Byte]) =>
      if (bytes.isEmpty) Base58(bytes).str.isEmpty
      else Base58(bytes).str.nonEmpty
    }.toProperties("create base58 string from bytearray")

    val invert = forAll { (bytes: Array[Byte]) =>
      val base58 = Base58(bytes)
      Base58.toByteArray(base58).map(_.toSeq) == Success(bytes.toSeq)
    }.toProperties("inverted bytearray should be equal")

    val fail = forAll { (s: String) =>
      Base58.fromString(s).isFailure
    }(invalidStr).toProperties("invalid base58 string")

    Properties.list(apply, invert, fail)
  }
}
