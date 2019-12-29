package org.finnpic

import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Properties}

object PicPropertySpec extends Properties("Pic.fromString") {
  val stringOf11AsciiCharactersG: Gen[String] = Gen.listOfN(11, Gen.asciiPrintableChar).map(_.mkString)

  val birthDatePartG: Gen[String] = Gen.listOfN(6, Gen.numChar).map(_.mkString)
  val signG: Gen[String] = Gen.oneOf("+", "-", "A")
  val individualNumberG: Gen[String] = Gen.listOfN(3, Gen.numChar).map(_.mkString)
  val controlCharacterG: Gen[String] = Gen.oneOf(Pic.controlChars).map(_.toString)
  val formallyValidPicG: Gen[String] = for {
    birthDatePart <- birthDatePartG
    sign <- signG
    individualNumber <- individualNumberG
    controlCharacter <- controlCharacterG
  } yield (birthDatePart + sign + individualNumber + controlCharacter)

  property("does not throw runtime exceptions for arbitrary strings of any length") = forAll { (s: String) =>
    val e: Either[String, Pic] = Pic(s)
    e match {
      case Left(_) => true
      case Right(pic) => pic.value == s.trim.toUpperCase
    }
  }

  property("does not throw runtime exceptions for arbitrary strings of length 11") = forAll(stringOf11AsciiCharactersG) { (s: String) =>
    val e: Either[String, Pic] = Pic(s)
    e match {
      case Left(errMsg) => true
      case Right(pic) => pic.value == s.trim.toUpperCase
    }
  }

  property("does not throw runtime exceptions for arbitrary formally valid pics") = forAll(formallyValidPicG) { (s: String) =>
    val e: Either[String, Pic] = Pic(s)
    e match {
      case Left(_) => true
      case Right(pic) => {
        pic.value == s.trim.toUpperCase && pic.birthDate.toEpochDay > 0
      }
    }
  }
}
