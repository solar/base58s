package org.sazabi.base58

import scala.annotation.tailrec
import scala.collection.mutable.StringBuilder
import scala.util.{ Failure, Success, Try }

case class Base58String private[base58] (str: String)

trait Base58 {
  private[this] val Base58Chars =
    "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

  private[this] val Base58Size = Base58Chars.size

  private[this] def index(c: Char) = c match {
    case c if c <= '9' && c >= '1' => Some(c - '1')
    case c if c <= 'k' && c >= 'a' => Some(c - 'a' + 33)
    case c if c <= 'z' && c >= 'm' => Some(c - 'm' + 44)
    case c if c >= 'A' && c <= 'H' => Some(c - 'A' + 9)
    case c if c >= 'J' && c <= 'N' => Some(c - 'J' + 17)
    case c if c >= 'P' && c <= 'Z' => Some(c - 'P' + 22)
    case _ => None
  }

  def apply(bytes: Array[Byte]): Base58String = {
    if (bytes.isEmpty) Base58String("")
    else {
      val bi = BigInt(1, bytes)

      val s = new StringBuilder

      @tailrec
      def append(rest: BigInt) {
        val div = rest / Base58Size
        val mod = rest % Base58Size
        s.insert(0, Base58Chars(mod.intValue))
        if (div > 0) append(div)
      }

      append(bi)

      val zeros = bytes.indexWhere(_ != 0)
      0 until zeros foreach { _ => s.insert(0, Base58Chars(0)) }

      Base58String(s.toString)
    }
  }

  def fromString(str: String): Try[Base58String] = {
    val i = str.indexWhere(index(_).isEmpty)

    if (i == -1) Success(Base58String(str))
    else Failure(InvalidCharacterException(str(i), i))
  }

  def toByteArray(b58: Base58String): Try[Array[Byte]] = {
    if (b58.str.isEmpty) Success(Array.empty[Byte])
    else {
      def toBytes = (in: String) => Try {//: String => Try[Array[Byte]] = in => \/.fromTryCatchNonFatal {
        val size = in.size
        in.zipWithIndex.foldRight(BigInt(0)) { (c, bi) =>
          index(c._1).map { i =>
            bi + (BigInt(i) * BigInt(Base58Size).pow(size - 1 - c._2))
          }.getOrElse(throw InvalidCharacterException(c._1, c._2))
        }.toByteArray.dropWhile(_ == 0)
      }

      val (z, in) = b58.str.span(_ == Base58Chars.head)
      val zeros = z.map(_ => 0: Byte).toArray

      if (in.isEmpty) Success(zeros)
      else toBytes(in).map { bytes => zeros ++ bytes }
    }
  }

  private[this] val invalidChar: Char => Boolean = !Base58Chars.contains(_)

  case class InvalidCharacterException(char: Char, index: Int)
    extends IllegalArgumentException(
      s"An invalid character ($char)) at index $index")
}

object Base58 extends Base58
