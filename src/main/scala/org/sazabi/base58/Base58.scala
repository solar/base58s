package org.sazabi.base58

import scala.annotation.tailrec
import scala.collection.mutable.StringBuilder

import scalaz._

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

  def fromString(str: String): Throwable \/ Base58String = {
    val i = str.indexWhere(index(_).isEmpty)

    if (i == -1) \/-(Base58String(str))
    else -\/(InvalidCharacterException(str(i), i))
  }

  def toByteArray(b58: Base58String): Throwable \/ Array[Byte] = {
    val bytes: Throwable \/ Array[Byte] = \/.fromTryCatchNonFatal {
      val size = b58.str.size
      b58.str.zipWithIndex.foldRight(BigInt(0)) { (c, bi) =>
        index(c._1).map { i =>
          bi + (BigInt(i) * BigInt(Base58Size).pow(size - 1 - c._2))
        }.getOrElse(throw InvalidCharacterException(c._1, c._2))
      }.toByteArray
    }

    bytes map { bytes =>
      val offset = if (bytes.size > 2 && bytes(0) == 0 && bytes(1) < 0) 1 else 0;

      val zeros = b58.str.indexWhere(_ != Base58Chars.head)

      val dest = new Array[Byte](bytes.size - offset + zeros)

      Array.copy(bytes, offset, dest, zeros, dest.size - zeros)
      dest
    }
  }

  private[this] val invalidChar: Char => Boolean = !Base58Chars.contains(_)

  case class InvalidCharacterException(char: Char, index: Int)
    extends IllegalArgumentException(
      s"An invalid character ($char)) at index $index")
}

object Base58 extends Base58
