package org.finnpic.businessid
import scala.collection.immutable

class BusinessId(input: String) {
  val value: String = input.trim.toUpperCase
}

object BusinessId {
  def apply(input: String): Either[String, BusinessId] = fromString(input)

  def fromString(input: String): Either[String, BusinessId] = {
    input.trim().toUpperCase match {
      case s: String if s.length != 9 => Left(s"Invalid business id: ''. Business id should have 9 characters, but was 0 characters.")
      case s: String if s.length == 9 => createFromStringOfCorrectLength(input)
    }
  }

  def createFromStringOfCorrectLength(input: String): Either[String, BusinessId] = {
    assert(input.length == 9)
    if (input.matches("""[\d]{7}-[\d]""")) {
      checksum(input) match {
        case (true, _, _) => Right(new BusinessId(input))
        case (false, was, shouldHaveBeen) => Left(s"Invalid business id: '${input}'. The checksum character '${was}' is wrong: it should be '${shouldHaveBeen}'.")
      }
    } else {
      Left(s"Invalid business id: '${input}'. Business id should contain only digits and a dash.")
    }
  }

  def checksum(input: String): (Boolean, Int, Int) = {
    val weights = List(7, 9, 10, 5, 8, 4, 2)
    val numbers = input.substring(0,7).toCharArray.map(_.asDigit)
    val zipped: immutable.Seq[(Int, Int)] = weights.zip(numbers)
    val multiplied = zipped.map {
      case (w, n) => w * n
    }
    val sum = multiplied.sum
    val sumMod11 = sum % 11
    val inputChecksumDigit = input.charAt(input.length-1).asDigit

    val expectedChecksumDigit = sumMod11 match {
      case 0 => 0
      case 1 => ???
      case i if 2 until 10 contains i => 11 - i
    }

    (expectedChecksumDigit == inputChecksumDigit, inputChecksumDigit, expectedChecksumDigit)
  }
}