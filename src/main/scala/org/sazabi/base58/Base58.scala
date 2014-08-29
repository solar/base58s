package org.sazabi.base58

import scala.annotation.tailrec
import scala.collection.mutable.StringBuilder

import scalaz._

case class Base58String private[base58] (str: String)

trait Base58 {
  private[this] val Base58Chars =
    "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

  private[this] val Base58Size = Base58Chars.size

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
    val i = str.indexWhere(invalidChar)

    if (i == -1) \/-(Base58String(str))
    else -\/(InvalidCharacterException(str(i), i))
  }

  def toByteArray(b58: Base58String): Throwable \/ Array[Byte] = {
    val bytes: Throwable \/ Array[Byte] = {
      val size = b58.str.size

      \/.fromTryCatchNonFatal {
        (0 until size).map { i =>
          val c = b58.str(i)
          val index = Base58Chars.indexOf(c)
          if (index == -1) throw InvalidCharacterException(c, i)
          BigInt(index) * BigInt(Base58Size).pow(size - 1 - i)
        }.sum.toByteArray
      }
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
