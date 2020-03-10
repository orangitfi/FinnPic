package org.finnpic

/**
  * Something that can be parsed from a String to a value object of type [[T]].
  * @tparam T The class of the value object which is the result of successful parsing.
  */
trait Parsable[T] {

  /**
    * Create a [[T]] from an input String, returning an [[scala.util.Either]].
    *
    * See also [[fromStringUnsafe]] for cases where you are sure that the input is a valid [[T]]
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
    * @param input a valid String representation of [[T]].
    * @return Left(String) if the given String is not a valid String representation of [[T]], return a scala.util.Left containing an error message.
    *         Right(T) if the given String is a valid String representation of [[T]], return a scala.util.Right containing the [[T]] object.
    */
  def fromString(input: String): Either[String, T]

  /**
    * Create a [[T]] from an input String, throwing an [[scala.IllegalArgumentException]] if the input is not a valid String representation of [[T]].
    *
    * See also:
    *   - [[fromString]] for cases where you are not sure if the input is a valid [[T]] (for example,
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
    * @param input a valid String representation of [[T]].
    * @return a [[T]] object if the input is a valid String representation of [[T]].
    * @throws scala.IllegalArgumentException if the input is not a valid String representation of [[T]].
    */
  def fromStringUnsafe(input: String): T = {
    fromString(input) match {
      case Left(errorMessage) =>
        throw new IllegalArgumentException(errorMessage)
      case Right(value) => value
    }
  }

  /**
    * A shorter alias for [[fromStringUnsafe]].
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
  def fromStringU(input: String): T = fromStringUnsafe(input)
}
