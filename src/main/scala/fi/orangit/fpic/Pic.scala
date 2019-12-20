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

/**
 * The gender of the person, deduced from PIC. There are only two options present in the PIC,
 * [[Male]] or [[Female]] - the PIC does not support more genders yet.
 */
sealed trait Gender

/**
 * A male person.
 */
object Male extends Gender

/**
 * A female person.
 */
object Female extends Gender

/**
 * Contains the factory methods for creating objects of class [[Pic]].
 * Follows the "smart constructor" pattern, familiar from Haskell.
 */
object Pic {
  /**
   * Create a Pic from an input String, returning an [[scala.util.Either]].
   *
   * See also [[fromStringUnsafe]] for cases where you are sure that the input is a valid PIC
   * (or you are willing to handle exceptions).
   *
   * @param input a valid Personal Identity Code as a String.
   * @return Left(String) if the given String is not a valid PIC, return a [[scala.util.Left]] containing an error message.
   *         Right(Pic) if the given String is a valid PIC, return a [[scala.util.Right]] containing the [[Pic]] object.
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
   *
   * - [[fromString]] for cases where you are not sure if the input is a valid PIC (for example,
   * the input comes from a user), which returns an Either for you to handle.
   *
   * - [[fromStringU]] for a shorter named alias of this function.
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
   */
  def fromStringU(input: String): Pic = fromStringUnsafe(input)

  /**
   * Here we have certainty that parameter `cleanedInput` is 11 chars long.
   */
  private def createFromStringOfCorrectLength(originalInput: String, cleanedInput: String): Either[String, Pic] = {
    // Note: These substring splits cannot fail, since the string is already confirmed as being 11 chars long.
    val ddMmYyPart = cleanedInput.substring(0, 6)
    val sign = cleanedInput.substring(6, 7)
    val individualNumber = cleanedInput.substring(7, 10)
    val controlCharacter = cleanedInput.substring(10, 11)
    createFromSubstrings(originalInput, cleanedInput, ddMmYyPart, sign, individualNumber, controlCharacter)
  }

  /**
   * Here we have certainty that all the substrings are 11 chars long.
   */
  private def createFromSubstrings(originalInput: String, cleanedInput: String, ddMmYyPart: String, sign: String, individualNumber: String, controlCharacter: String): Either[String, Pic] = {
    if (!ddMmYyPart.matches("\\d{6}")) {
      Left(s"Invalid PIC: '${originalInput}'. The first six characters have to be numeric, but they were: '${ddMmYyPart}'.")
    } else if (!List('+', '-', 'A').contains(sign.charAt(0))) {
      Left(s"Invalid PIC: '${originalInput}'. The sign (7th character) must be +, - or A, now it was: '${sign}'.")
    } else if (Try(individualNumber.toInt).toOption.isEmpty) {
      Left(s"Invalid PIC: '${originalInput}'. The individual number (characters 8-10) must be numeric, now it was: '${individualNumber}'.")
    } else {
      createFromValidParts(originalInput, cleanedInput, ddMmYyPart, sign.charAt(0), individualNumber, controlCharacter.charAt(0))
    }
  }

  /**
   * Here we have certainty that all the parts are themselves valid, except for the control character.
   */
  private def createFromValidParts(originalInput: String, cleanedInput: String, ddMmYyPart: String, sign: Char, individualNumber: String, controlCharacter: Char): Either[String, Pic] = {
    // At this point the ddMmYyPart and individualNumber have been validated to be numeric, so we can just use .toLong directly without Try.
    val expectedControlCharacter: Char = calculateExpectedControlCharacter((ddMmYyPart + individualNumber).toLong)
    if (controlCharacter != expectedControlCharacter) {
      Left(s"Invalid PIC: '${originalInput}'. The control character ('${controlCharacter}') is wrong: it should be '${expectedControlCharacter}'.")
    } else {
      val gender = if (individualNumber.toInt % 2 == 0) Female else Male
      val century = sign match {
        case '+' => 1800
        case '-' => 1900
        case 'A' => 2000
      }
      val yearWithinCentury = ddMmYyPart.toString.substring(4, 6).toInt
      val birthYear = century + yearWithinCentury
      val birthMonth = ddMmYyPart.toString.substring(2, 4).toInt
      val birthDay = ddMmYyPart.toString.substring(0, 2).toInt
      Right(new Pic(cleanedInput, gender, birthYear, birthMonth, birthDay))
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
