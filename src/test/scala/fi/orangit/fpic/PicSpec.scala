package fi.orangit.fpic

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.util.Try

/**
 * See https://vrk.fi/en/personal-identity-code1 for specs.
 */
class PicSpec extends AnyFlatSpecLike with Matchers {
  val ValidPic1MaleBornIn1900s = "290877-1639"
  val ValidPic2FemaleBornIn1900s = "010781-190A"
  val ValidPic3FemaleBornIn2010s = "170214A6228"

  val validPics: List[String] = List(ValidPic1MaleBornIn1900s, ValidPic2FemaleBornIn1900s, ValidPic3FemaleBornIn2010s)

  behavior of "object Pic, method fromString"

  it should "reject an empty string" in {
    Pic.fromString("") should be(Left("Invalid PIC: ''. PIC should have 11 characters, but was 0 characters."))
  }

  it should "reject a PIC with non-numeric character in the first 6 places" in {
    Pic.fromString("2908X7-1639") should be(Left("Invalid PIC: '2908X7-1639'. The first six characters have to be numeric, but they were: '2908X7'."))
  }

  it should "reject a PIC where the delimiter char is something other than +, -, or A" in {
    Pic.fromString("290877X1639") should be(Left("Invalid PIC: '290877X1639'. The sign (7th character) must be +, - or A, now it was: 'X'."))
  }

  it should "reject a PIC where the individual number is not numeric" in {
    Pic.fromString("290877-16A9") should be(Left("Invalid PIC: '290877-16A9'. The individual number (characters 8-10) must be numeric, now it was: '16A'."))
  }

  it should "reject a PIC the control character is wrong" in {
    Pic.fromString("290877-1638") should be(Left("Invalid PIC: '290877-1638'. The control character ('8') is wrong: it should be '9'."))
  }

  it should "accept a valid PIC" in {
    validPics.foreach(s =>
      Pic.fromString(s) match {
        case Left(errorMsg: String) => fail(errorMsg)
        case Right(pic) => pic.value should be(s)
      })
  }

  it should "uppercase all characters to achieve a canonical representation" in {
    Pic.fromStringU("010781-190a").value should be("010781-190A")
    Pic.fromStringU("170214a6228").value should be("170214A6228")
  }

  behavior of "object Pic, method fromStringUnsafe / fromStringU"

  it should "throw an IllegalArgumentException if the input is not a valid PIC" in {
    val t = Try(Pic.fromStringU("290877-1640")).toEither
    t match {
      case Left(err) => {
        err shouldBe an[IllegalArgumentException]
        err.getMessage should be("Invalid PIC: '290877-1640'. The control character ('0') is wrong: it should be 'A'.")
      }
      case Right(value) => fail("An IllegalArgumentException should have been thrown, but was not.")
    }
  }

  behavior of "class Pic"

  it should "know the supposed gender of the person in question" in {
    Pic.fromStringU(ValidPic1MaleBornIn1900s).gender should be(Male)
    Pic.fromStringU(ValidPic2FemaleBornIn1900s).gender should be(Female)
    Pic.fromStringU(ValidPic3FemaleBornIn2010s).gender should be(Female)
  }

  it should "know the birth year of the person in question" in {
    Pic.fromStringU(ValidPic1MaleBornIn1900s).birthYear should be(1977)
    Pic.fromStringU(ValidPic2FemaleBornIn1900s).birthYear should be(1981)
    Pic.fromStringU(ValidPic3FemaleBornIn2010s).birthYear should be(2014)
  }

  it should "know the birth month of the person in question" in {
    Pic.fromStringU(ValidPic1MaleBornIn1900s).birthMonth should be(8)
    Pic.fromStringU(ValidPic2FemaleBornIn1900s).birthMonth should be(7)
    Pic.fromStringU(ValidPic3FemaleBornIn2010s).birthMonth should be(2)
  }

  it should "know the birth day (of month) of the person in question" in {
    Pic.fromStringU(ValidPic1MaleBornIn1900s).birthDay should be(29)
    Pic.fromStringU(ValidPic2FemaleBornIn1900s).birthDay should be(1)
    Pic.fromStringU(ValidPic3FemaleBornIn2010s).birthDay should be(17)
  }

  behavior of "Pic.toString"

  it should "just show the original pic (the String value) without any decoration" in {
    Pic.fromStringU(ValidPic1MaleBornIn1900s).toString should be(ValidPic1MaleBornIn1900s)
    Pic.fromStringU(ValidPic2FemaleBornIn1900s).toString should be(ValidPic2FemaleBornIn1900s)
    Pic.fromStringU(ValidPic3FemaleBornIn2010s).toString should be(ValidPic3FemaleBornIn2010s)
  }
}
