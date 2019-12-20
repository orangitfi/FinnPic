package fi.orangit.fpic

import scala.util.Try

/**
 * PIC = Personal Identity Code, "henkilötunnus" in Finnish.
 *
 * See https://vrk.fi/en/personal-identity-code1 for specs.
 *
 * The constructor is private on purpose, so that Pics can only
 * be created via the companion object (the so called
 * "smart constructor" pattern). This prevents instances which
 * are in an illegal state. Every Pic object can thus be
 * treated as a valid Pic.
 */
class Pic private(_asString: String, _gender: Gender, _birthYear: Int, _birthMonth: Int, _birthDay: Int) {
  val value: String = _asString

  val gender: Gender = _gender

  val birthYear: Int = _birthYear

  val birthMonth: Int = _birthMonth

  val birthDay: Int = _birthDay

  override def toString: String = value

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Pic =>
        that.value.equals(this.value)
      case _ =>
        false
    }
  }

  override def hashCode(): Int = value.hashCode
}

sealed trait Gender

object Male extends Gender

object Female extends Gender

object Pic {
  def fromString(input: String): Either[String, Pic] = {
    input.trim().toUpperCase match {
      case s: String if s.length != 11 => Left(s"Invalid PIC: '${s}'. PIC should have 11 characters, but was ${s.length} characters.")
      case s: String => {
        // Note: These substring splits cannot fail, since the string is already confirmed as being 11 chars long.
        val ddMmYyPart = s.substring(0, 6)
        val sign = s.substring(6, 7)
        val individualNumber = s.substring(7, 10)
        val controlCharacter = s.substring(10, 11)
        if (!ddMmYyPart.matches("\\d{6}")) {
          Left(s"Invalid PIC: '${s}'. The first six characters have to be numeric, but they were: '${ddMmYyPart}'.")
        } else if (!List('+', '-', 'A').contains(sign.charAt(0))) {
          Left(s"Invalid PIC: '${s}'. The sign (7th character) must be +, - or A, now it was: '${sign}'.")
        } else if (Try(individualNumber.toInt).toOption.isEmpty) {
          Left(s"Invalid PIC: '${s}'. The individual number (characters 8-10) must be numeric, now it was: '${individualNumber}'.")
        } else {
          // At this point the ddMmYyPart and individualNumber have been validated to be numeric, so we can just use .toLong directly without Try.
          val expectedControlCharacter: String = calculateExpectedControlCharacter((ddMmYyPart + individualNumber).toLong).toString
          if (controlCharacter != expectedControlCharacter) {
            Left(s"Invalid PIC: '${s}'. The control character ('${controlCharacter}') is wrong: it should be '${expectedControlCharacter}'.")
          } else {
            val gender = if (individualNumber.toInt % 2 == 0) Female else Male
            val century = sign match {
              case "+" => 1800
              case "-" => 1900
              case "A" => 2000
            }
            val yearWithinCentury = ddMmYyPart.substring(4, 6).toInt
            val birthYear = century + yearWithinCentury
            val birthMonth = ddMmYyPart.substring(2, 4).toInt
            val birthDay = ddMmYyPart.substring(0, 2).toInt
            Right(new Pic(s, gender, birthYear, birthMonth, birthDay))
          }
        }
      }
    }
  }

  /**
   * The possible control characters. See https://vrk.fi/en/personal-identity-code1 for more information about the control character calculation.
   */
  private val controlChars: Array[Char] = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y')

  /**
   * The input to this method is the birth date part (290877 in 290877-1639)
   * concatenated with the individualNumber (163 in 290877-1639). In this example,
   * the returned char would be 9.
   *
   * See https://vrk.fi/en/personal-identity-code1 for more information about the control character calculation.
   *
   * @param input see the description of input above.
   * @return the expected control character for the input given.
   */
  private def calculateExpectedControlCharacter(input: Long): Char = {
    val remainder: Int = (input % 31).toInt
    controlChars(remainder)
  }
}
