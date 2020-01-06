package org.finnpic

import java.time.{Clock, LocalDate, Period}

import scala.util.{Failure, Success, Try}

/**
 * PIC = Personal Identity Code, "henkilotunnus" in Finnish.
 *
 * See https://vrk.fi/en/personal-identity-code1 for specs.
 *
 * The constructor is private on purpose, so that Pics can only
 * be created via the companion object (the so called
 * "smart constructor" pattern). This prevents instances which
 * are in an illegal state. Every Pic object can thus be
 * treated as a valid Pic.
 *
 * See the companion object for how to create instances of Pic.
 *
 * Some examples of [[Pic]] creation:
 * {{{
 * >>> Pic("070377-281V")
 * Right(070377-281V)
 *
 * >>> Pic("070377-281")
 * Left(Invalid PIC: '070377-281'. PIC should have 11 characters, but was 10 characters.)
 *
 * >>> Pic("070377-2818")
 * Left(Invalid PIC: '070377-2818'. The control character ('8') is wrong: it should be 'V'.)
 *
 * >>> Pic.fromStringUnsafe("070377-281V")
 * 070377-281V
 *
 * // Pic.fromStringU is just a shorter alias for Pic.fromStringUnsafe.
 * >>> Pic.fromStringU("070377-281V")
 * 070377-281V
 *
 * // If you give Pic.fromStringUnsafe (or Pic.fromStringU) an invalid PIC, they throw an IllegalArgumentException.
 * }}}
 */
class Pic private(mValue: String, mGender: Gender, mBirthYear: Int, mBirthMonth: Int, mBirthDay: Int) {
  /**
   * The PIC String used to create this Pic object itself, trimmed and in uppercase.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V").value
   * 070377-281V
   *
   * >>> Pic.fromStringU("211114a664e").value
   * 211114A664E
   * }}}
   */
  val value: String = mValue

  /**
   * The [[Gender]] of the person whose PIC this is. Currently this can be only [[Male]] or [[Female]]; this
   * is a restriction which comes directly from the PIC specification.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V").gender
   * Male
   *
   * >>> Pic.fromStringU("211114A664E").gender
   * Female
   * }}}
   */
  val gender: Gender = mGender

  /**
   * The birth year of the person whose PIC this is.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V").birthYear
   * 1977
   *
   * >>> Pic.fromStringU("211114A664E").birthYear
   * 2014
   * }}}
   */
  val birthYear: Int = mBirthYear

  /**
   * The birth month of the person whose PIC this is. 1-based representation, so
   * January is 1 and December is 12.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V").birthMonth
   * 3
   *
   * >>> Pic.fromStringU("211114A664E").birthMonth
   * 11
   * }}}
   */
  val birthMonth: Int = mBirthMonth

  /**
   * The day of month of the birth of the person whose PIC this is.
   * It is not a typo that this method is called 'birthDay', not 'birthday'.
   * They are different concepts: this contains only the day of month,
   * whereas the concept of 'birthday' includes the whole date.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V").birthDay
   * 7
   *
   * >>> Pic.fromStringU("211114A664E").birthDay
   * 21
   * }}}
   */
  val birthDay: Int = mBirthDay

  def birthDate: LocalDate = {
    LocalDate.of(birthYear, birthMonth, birthDay)
  }

  def ageAt(at: LocalDate): Period = {
    Period.between(birthDate, at)
  }

  def ageInYearsAt(at: LocalDate): Int = {
    ageAt(at).getYears
  }

  def personIsOfFinnishLegalAgeAt(at: LocalDate): Boolean = {
    ageInYearsAt(at) >= Pic.finnishLegalAge
  }

  /**
   * The canonical string representation of the PIC. Usually the same String
   * which was used to create this object.
   *
   * Example:
   * {{{
   * >>> Pic.fromStringU("070377-281V").toString
   * 070377-281V
   * }}}
   *
   * @return the canonical string representation of the PIC.
   */
  override def toString: String = value

  /**
   * Equals is true if the canonical string representation matches.
   * Always false if the other object is not instance of [[Pic]].
   *
   * @param obj another object.
   * @return true if the string matches, false if not.
   */
  override def equals(obj: Any): Boolean = {
    obj match {
      case that: Pic =>
        that.value.equals(this.value)
      case _ =>
        false
    }
  }

  /**
   * The hashCode() function of [[Pic]] only delegates the call to [[value]].
   *
   * Example:
   * {{{
   * >>> Pic.fromStringU("070377-281V").hashCode == "070377-281V".hashCode
   * true
   * }}}
   *
   * @return
   */
  override def hashCode(): Int = value.hashCode
}

/**
 * The gender of the person, deduced from PIC. There are only two options present in the PIC,
 * [[Male]] or [[Female]] - the PIC does not support more genders yet.
 */
sealed trait Gender

/**
 * A male person.
 */
case object Male extends Gender

/**
 * A female person.
 */
case object Female extends Gender

/**
 * Contains the factory methods for creating objects of class [[Pic]].
 * Follows the "smart constructor" pattern, familiar from Haskell.
 *
 * Some examples of [[Pic]] creation:
 * {{{
 * >>> Pic("070377-281V")
 * Right(070377-281V)
 *
 * >>> Pic("070377-281")
 * Left(Invalid PIC: '070377-281'. PIC should have 11 characters, but was 10 characters.)
 *
 * >>> Pic("070377-2818")
 * Left(Invalid PIC: '070377-2818'. The control character ('8') is wrong: it should be 'V'.)
 *
 * >>> Pic.fromStringUnsafe("070377-281V")
 * 070377-281V
 *
 * // Pic.fromStringU is just a shorter alias for Pic.fromStringUnsafe.
 * >>> Pic.fromStringU("070377-281V")
 * 070377-281V
 *
 * // If you give Pic.fromStringUnsafe (or Pic.fromStringU) an invalid PIC, they throw an IllegalArgumentException.
 * }}}
 */
object Pic {
  val finnishLegalAge: Int = 18

  /**
   * Create a Pic from an input String, see [[fromString(input:String):Either*]].
   */
  def apply(input: String): Either[String, Pic] = fromString(input)

  /**
   * Create a Pic from an input String, returning an [[scala.util.Either]].
   *
   * See also [[fromStringUnsafe]] for cases where you are sure that the input is a valid PIC
   * (or you are willing to handle exceptions).
   *
   * Examples:
   * {{{
   * >>> Pic("070377-281V")
   * Right(070377-281V)
   *
   * >>> Pic("070377-281")
   * Left(Invalid PIC: '070377-281'. PIC should have 11 characters, but was 10 characters.)
   *
   * >>> Pic("070377-2818")
   * Left(Invalid PIC: '070377-2818'. The control character ('8') is wrong: it should be 'V'.)
   * }}}
   *
   * @param input a valid Personal Identity Code as a String.
   * @return Left(String) if the given String is not a valid PIC, return a scala.util.Left containing an error message.
   *         Right(Pic) if the given String is a valid PIC, return a scala.util.Right containing the [[Pic]] object.
   */
  def fromString(input: String): Either[String, Pic] = {
    input.trim().toUpperCase match {
      case s: String if s.length != 11 => Left(s"Invalid PIC: '${s}'. PIC should have 11 characters, but was ${s.length} characters.")
      case s: String if s.length == 11 => createFromStringOfCorrectLength(input, s)
    }
  }

  /**
   * Create a Pic from an input String, throwing an [[scala.IllegalArgumentException]] if the input is not a valid PIC.
   *
   * See also:
   *   - [[fromString]] for cases where you are not sure if the input is a valid PIC (for example,
   * the input comes from a user), which returns an Either for you to handle.
   *   - [[fromStringU]] for a shorter named alias of this function.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringUnsafe("070377-281V")
   * 070377-281V
   *
   * >>> // Pic.fromStringUnsafe("foo") would throw an IllegalArgumentException.
   * }}}
   *
   * @param input a valid Personal Identity Code as a String.
   * @return a Pic object if the input is a valid PIC.
   * @throws scala.IllegalArgumentException if the input is not a valid PIC.
   */
  def fromStringUnsafe(input: String): Pic = {
    fromString(input) match {
      case Left(errorMessage) => throw new IllegalArgumentException(errorMessage)
      case Right(pic) => pic
    }
  }

  /**
   * A shorter alias for [[Pic.fromStringUnsafe]].
   * See the documentation for that function.
   *
   * Examples:
   * {{{
   * >>> Pic.fromStringU("070377-281V")
   * 070377-281V
   *
   * >>> // Pic.fromStringU("foo") would throw an IllegalArgumentException.
   * }}}
   */
  def fromStringU(input: String): Pic = fromStringUnsafe(input)

  /**
   * Here we have certainty that parameter `cleanedInput` is 11 chars long.
   */
  private def createFromStringOfCorrectLength(originalInput: String, cleanedInput: String): Either[String, Pic] = {
    // Note: These substring splits cannot fail, since the string is already confirmed as being 11 chars long.
    val birthDatePart = cleanedInput.substring(0, 6)
    val sign = cleanedInput.substring(6, 7)
    val individualNumber = cleanedInput.substring(7, 10)
    val controlCharacter = cleanedInput.substring(10, 11)
    createFromSubstrings(PicParts(originalInput, cleanedInput, birthDatePart, sign, individualNumber, controlCharacter))
  }

  private case class PicParts(originalInput: String,
                              cleanedInput: String,
                              birthDatePart: String,
                              sign: String,
                              individualNumber: String,
                              controlCharacter: String)

  private case class ValidationRule(predicate: PicParts => Boolean, errorMessageGenerator: PicParts => String)

  private val birthDatePartMustBeNumeric = ValidationRule(
    pp =>
      pp.birthDatePart.matches("\\d{6}"),
    pp =>
      s"Invalid PIC: '${pp.originalInput}'. The first six characters have to be numeric, but they were: '${pp.birthDatePart}'.")

  private val dayOfBirthDatePartMustBeInRangeOf1To31 = ValidationRule(
    pp => {
      val day = pp.birthDatePart.substring(0, 2).toInt
      day >= 1 && day <= 31
    },
    pp =>
      s"Invalid PIC: '${pp.originalInput}'. The day part of the birth date is wrong: it should be 01-31, but was: '${pp.birthDatePart.substring(0, 2)}'."
  )

  private val monthOfBirthDatePartMustBeInRangeOf1To12 = ValidationRule(
    pp => {
      val month = pp.birthDatePart.substring(2, 4).toInt
      month >= 1 && month <= 12
    },
    pp =>
      s"Invalid PIC: '${pp.originalInput}'. The month part of the birth date is wrong: it should be 01-12, but was: '${pp.birthDatePart.substring(2, 4)}'."
  )

  private val signMustHaveAcceptableValue = ValidationRule(
    pp =>
      List('+', '-', 'A').contains(pp.sign.charAt(0)),
    pp =>
      s"Invalid PIC: '${pp.originalInput}'. The sign (7th character) must be +, - or A, now it was: '${pp.sign}'."
  )

  private val individualNumberMustBeNumeric = ValidationRule(
    pp =>
      Try(pp.individualNumber.toInt).toOption.isDefined,
    pp =>
      s"Invalid PIC: '${pp.originalInput}'. The individual number (characters 8-10) must be numeric, now it was: '${pp.individualNumber}'."
  )

  private val validationRules = List(
    birthDatePartMustBeNumeric,
    dayOfBirthDatePartMustBeInRangeOf1To31,
    monthOfBirthDatePartMustBeInRangeOf1To12,
    signMustHaveAcceptableValue,
    individualNumberMustBeNumeric)

  /**
   * Here we have certainty that all the substrings, when concatenated together, form a string of 11 chars.
   */
  private def createFromSubstrings(pp: PicParts): Either[String, Pic] = {
    val failedValidationRule = validationRules.find(vr => !vr.predicate(pp))
    failedValidationRule match {
      case Some(rule) => Left(rule.errorMessageGenerator(pp))
      case None => createFromValidParts(pp)
    }
  }

  /**
   * Here we have certainty that all the parts are themselves valid, except for the control character.
   */
  private def createFromValidParts(pp: PicParts): Either[String, Pic] = {
    // At this point the birthDatePart and individualNumber have been validated to be numeric, so we can just use .toLong directly without Try.
    val expectedControlCharacter: Char = calculateExpectedControlCharacter((pp.birthDatePart + pp.individualNumber).toLong)
    if (pp.controlCharacter.charAt(0) != expectedControlCharacter) {
      Left(s"Invalid PIC: '${pp.originalInput}'. The control character ('${pp.controlCharacter}') is wrong: it should be '${expectedControlCharacter}'.")
    } else {
      val gender = if (pp.individualNumber.toInt % 2 == 0) Female else Male
      val century = pp.sign.charAt(0) match {
        case '+' => 1800
        case '-' => 1900
        case 'A' => 2000
      }
      val yearWithinCentury = pp.birthDatePart.toString.substring(4, 6).toInt
      val birthYear = century + yearWithinCentury
      val birthMonth = pp.birthDatePart.toString.substring(2, 4).toInt
      val birthDay = pp.birthDatePart.toString.substring(0, 2).toInt
      val candidate: Pic = new Pic(pp.cleanedInput, gender, birthYear, birthMonth, birthDay)
      // Check that the birth date is not impossible (e.g. not in the calendar,
      // like "30.2.1977").
      Try(candidate.birthDate) match {
        case Success(_) => Right(candidate)
        case Failure(_) => Left(s"Invalid PIC: '${pp.originalInput}'. The birth date is impossible, this day does not exist on the calendar: '${pp.birthDatePart}'.")
      }
    }
  }

  /**
   * The possible control characters. See https://vrk.fi/en/personal-identity-code1 for more information about the control character calculation.
   */
  private[finnpic] val controlChars: Array[Char] = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y')

  /**
   * The input to this method is the birth date part (070377 in 070377-281V)
   * concatenated with the individualNumber (281 in 070377-281V). In this example,
   * the returned char would be 9.
   *
   * See https://vrk.fi/en/personal-identity-code1 for more information about the control character calculation.
   *
   * @param input see the description of input above.
   * @return the expected control character for the input given.
   */
  private[finnpic] def calculateExpectedControlCharacter(input: Long): Char = {
    val remainder: Int = (input % 31).toInt
    controlChars(remainder)
  }
}
