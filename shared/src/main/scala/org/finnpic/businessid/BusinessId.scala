package org.finnpic.businessid

import org.finnpic.{Parsable, Pic}

import scala.collection.immutable

class BusinessId(input: String) {
  val value: String = input

  /**
   * The canonical string representation of the business id. Usually the same String
   * which was used to create this object.
   *
   * Example:
   * {{{
   * >>> BusinessId.fromStringU("2933973-7").toString
   * 2933973-7
   * }}}
   *
   * @return the canonical string representation of the business id.
   */
  override def toString: String = value

  /**
   * Equals is true if the canonical string representation matches.
   * Always false if the other object is not instance of [[BusinessId]].
   *
   * @param obj another object.
   * @return true if the string matches, false if not.
   */
  override def equals(obj: Any): Boolean = {
    obj match {
      case that: BusinessId =>
        that.value.equals(this.value)
      case _ =>
        false
    }
  }

  /**
   * The hashCode() function of [[BusinessId]] only delegates the call to [[value]].
   *
   * Example:
   * {{{
   * >>> BusinessId.fromStringU("2933973-7").hashCode == "2933973-7".hashCode
   * true
   * }}}
   *
   * @return
   */
  override def hashCode(): Int = value.hashCode
}

object BusinessId extends Parsable[BusinessId] {
  def apply(input: String): Either[String, BusinessId] = fromString(input)

  def fromString(input: String): Either[String, BusinessId] = {
    val canonized = input.trim
    canonized match {
      case s: String if s.length != 9 =>
        Left(
          s"Invalid business id: ''. Business id should have 9 characters, but was 0 characters."
        )
      case s: String if s.length == 9 =>
        createFromStringOfCorrectLength(input, canonized)
    }
  }

  def createFromStringOfCorrectLength(
    originalInput: String,
    canonizedInput: String
  ): Either[String, BusinessId] = {
    assert(canonizedInput.length == 9)
    if (canonizedInput.matches("""[\d]{7}-[\d]""")) {
      checksum(canonizedInput) match {
        case ChecksumOk => Right(new BusinessId(canonizedInput))
        case InvalidChecksumDigitInInput(was, shouldHaveBeen) =>
          Left(
            s"Invalid business id: '${originalInput}'. The checksum character '${was}' is wrong: it should be '${shouldHaveBeen}'."
          )
        case SumMod11ResultsIn1 =>
          Left(
            s"Invalid business id: '${originalInput}'. The checksum value of 1 is not allowed."
          )
      }
    } else {
      Left(
        s"Invalid business id: '${originalInput}'. Business id should contain only digits and a dash."
      )
    }
  }

  /**
   * See http://tarkistusmerkit.teppovuori.fi/tarkmerk.htm#y-tunnus2 for specs.
   */
  def checksum(input: String): ChecksumValidationResult = {
    val weights = List(7, 9, 10, 5, 8, 4, 2)
    val numbers = input.substring(0, 7).toCharArray.map(_.asDigit)
    val zipped: immutable.Seq[(Int, Int)] = weights.zip(numbers)
    val multiplied = zipped.map {
      case (w, n) => w * n
    }
    val sum = multiplied.sum
    val sumMod11 = sum % 11
    val inputChecksumDigit = input.charAt(input.length - 1).asDigit

    val expectedChecksumDigit: Either[ChecksumValidationResult, Int] =
      sumMod11 match {
        case 0                          => Right(0)
        case 1                          => Left(SumMod11ResultsIn1)
        case i if 2 until 10 contains i => Right(11 - i)
      }

    expectedChecksumDigit match {
      case Left(invalidResult) => invalidResult
      case Right(expectedDigit) if inputChecksumDigit == expectedDigit =>
        ChecksumOk
      case Right(expectedDigit) =>
        InvalidChecksumDigitInInput(inputChecksumDigit, expectedDigit)
    }
  }

  sealed trait ChecksumValidationResult

  case object ChecksumOk extends ChecksumValidationResult
  case class InvalidChecksumDigitInInput(was: Int, shouldHaveBeen: Int)
      extends ChecksumValidationResult
  case object SumMod11ResultsIn1 extends ChecksumValidationResult
}
