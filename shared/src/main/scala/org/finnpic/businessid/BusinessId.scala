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
      if (checksum(input)) {
        Right(new BusinessId(input))
      } else {
        Left(s"Invalid business id: '${input}'. The checksum character '8' is wrong: it should be '7'.")
      }

    } else {
      Left(s"Invalid business id: '${input}'. Business id should contain only digits and a dash.")
    }
  }

  def checksum(input: String): Boolean = {
    println(input)
    //7, 9, 10, 5, 8, 4 ja 2.
    val weights = List(7, 9, 10, 5, 8, 4, 2)
    val numbers = input.substring(0,7).toCharArray.map(_.asDigit)
    val zipped: immutable.Seq[(Int, Int)] = weights.zip(numbers)
    val multiplied = zipped.map {
      case (w, n) => w * n
    }
    println(numbers.toList)
    println(multiplied.toList)
    val sum = multiplied.sum
    println(sum)

    val checksum = input.charAt(input.length-1).asDigit
    println(checksum)
    println(input.charAt(input.length-1))
    sum % 11 == 11 - checksum
  }
}