package org.finnpic.businessid

class BusinessId(input: String) {

}

object BusinessId {
  def apply(input: String): Either[String, BusinessId] = fromString(input)

  def fromString(input: String): Either[String, BusinessId] = {
    input.trim().toUpperCase match {
      case s: String if s.length != 11 => Left(s"Invalid business id: ''. Business id should have 9 characters, but was 0 characters.")
      case s: String if s.length == 11 => ??? // createFromStringOfCorrectLength(input, s)
    }
  }
}