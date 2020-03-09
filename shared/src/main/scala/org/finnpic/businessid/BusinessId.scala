package org.finnpic.businessid

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
      Right(new BusinessId(input))
    } else {
      Left(s"Invalid business id: '${input}'. Business id should contain only digits and a dash.")
    }
  }
}