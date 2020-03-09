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
        case ChecksumOk => Right(new BusinessId(input))
        case InvalidChecksumDigitInInput(was, shouldHaveBeen) => Left(s"Invalid business id: '${input}'. The checksum character '${was}' is wrong: it should be '${shouldHaveBeen}'.")
        case SumMod11ResultsIn1 => Left(s"Invalid business id: '${input}'. The checksum value of 1 is not allowed.")
      }
    } else {
      Left(s"Invalid business id: '${input}'. Business id should contain only digits and a dash.")
    }
  }

  def checksum(input: String): ChecksumValidationResult = {
    val weights = List(7, 9, 10, 5, 8, 4, 2)
    val numbers = input.substring(0,7).toCharArray.map(_.asDigit)
    val zipped: immutable.Seq[(Int, Int)] = weights.zip(numbers)
    val multiplied = zipped.map {
      case (w, n) => w * n
    }
    val sum = multiplied.sum
    val sumMod11 = sum % 11
    val inputChecksumDigit = input.charAt(input.length-1).asDigit

    val expectedChecksumDigit: Either[ChecksumValidationResult, Int] = sumMod11 match {
      case 0 => Right(0)
      case 1 => Left(SumMod11ResultsIn1)
      case i if 2 until 10 contains i => Right(11 - i)
    }

    expectedChecksumDigit match {
      case Left(invalidResult) => invalidResult
      case Right(expectedDigit) if inputChecksumDigit == expectedDigit => ChecksumOk
      case Right(expectedDigit) => InvalidChecksumDigitInInput(inputChecksumDigit, expectedDigit)
    }
  }

  sealed trait ChecksumValidationResult

  case object ChecksumOk extends ChecksumValidationResult
  case class InvalidChecksumDigitInInput(was: Int, shouldHaveBeen: Int) extends ChecksumValidationResult
  case object SumMod11ResultsIn1 extends ChecksumValidationResult
}