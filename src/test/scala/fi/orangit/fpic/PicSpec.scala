package fi.orangit.fpic

import fi.orangit.fpic.Pic.{fromString, fromStringU}
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
  val ValidPic4MaleBornIn1800s = "290877+1639"

  val validPics: List[String] = List(ValidPic1MaleBornIn1900s, ValidPic2FemaleBornIn1900s, ValidPic3FemaleBornIn2010s)

  behavior of "object Pic, method fromString"

  it should "reject an empty string" in {
    fromString("") should be(Left("Invalid PIC: ''. PIC should have 11 characters, but was 0 characters."))
  }

  it should "reject a PIC with non-numeric character in the first 6 places" in {
    fromString("2908X7-1639") should be(Left("Invalid PIC: '2908X7-1639'. The first six characters have to be numeric, but they were: '2908X7'."))
  }

  it should "reject a PIC where the delimiter char is something other than +, -, or A" in {
    fromString("290877X1639") should be(Left("Invalid PIC: '290877X1639'. The sign (7th character) must be +, - or A, now it was: 'X'."))
  }

  it should "reject a PIC where the individual number is not numeric" in {
    fromString("290877-16A9") should be(Left("Invalid PIC: '290877-16A9'. The individual number (characters 8-10) must be numeric, now it was: '16A'."))
  }

  it should "reject a PIC the control character is wrong" in {
    fromString("290877-1638") should be(Left("Invalid PIC: '290877-1638'. The control character ('8') is wrong: it should be '9'."))
  }

  it should "accept a valid PIC" in {
    validPics.foreach(s =>
      fromString(s) match {
        case Left(errorMsg: String) => fail(errorMsg)
        case Right(pic) => pic.value should be(s)
      })
  }

  it should "uppercase all characters to achieve a canonical representation" in {
    fromStringU("010781-190a").value should be("010781-190A")
    fromStringU("170214a6228").value should be("170214A6228")
  }

  behavior of "object Pic, method fromStringUnsafe / fromStringU"

  it should "throw an IllegalArgumentException if the input is not a valid PIC" in {
    val t = Try(fromStringU("290877-1640")).toEither
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
    fromStringU(ValidPic1MaleBornIn1900s).gender should be(Male)
    fromStringU(ValidPic2FemaleBornIn1900s).gender should be(Female)
    fromStringU(ValidPic3FemaleBornIn2010s).gender should be(Female)
    fromStringU(ValidPic4MaleBornIn1800s).gender should be(Male)
  }

  it should "know the birth year of the person in question" in {
    fromStringU(ValidPic1MaleBornIn1900s).birthYear should be(1977)
    fromStringU(ValidPic2FemaleBornIn1900s).birthYear should be(1981)
    fromStringU(ValidPic3FemaleBornIn2010s).birthYear should be(2014)
    fromStringU(ValidPic4MaleBornIn1800s).birthYear should be(1877)
  }

  it should "know the birth month of the person in question" in {
    fromStringU(ValidPic1MaleBornIn1900s).birthMonth should be(8)
    fromStringU(ValidPic2FemaleBornIn1900s).birthMonth should be(7)
    fromStringU(ValidPic3FemaleBornIn2010s).birthMonth should be(2)
    fromStringU(ValidPic4MaleBornIn1800s).birthMonth should be(8)
  }

  it should "know the birth day (of month) of the person in question" in {
    fromStringU(ValidPic1MaleBornIn1900s).birthDay should be(29)
    fromStringU(ValidPic2FemaleBornIn1900s).birthDay should be(1)
    fromStringU(ValidPic3FemaleBornIn2010s).birthDay should be(17)
    fromStringU(ValidPic4MaleBornIn1800s).birthDay should be(29)
  }

  behavior of "Pic.toString()"

  it should "just show the original pic (the String value) without any decoration" in {
    fromStringU(ValidPic1MaleBornIn1900s).toString should be(ValidPic1MaleBornIn1900s)
    fromStringU(ValidPic2FemaleBornIn1900s).toString should be(ValidPic2FemaleBornIn1900s)
    fromStringU(ValidPic3FemaleBornIn2010s).toString should be(ValidPic3FemaleBornIn2010s)
    fromStringU(ValidPic4MaleBornIn1800s).toString should be(ValidPic4MaleBornIn1800s)
  }

  behavior of "Pic.equals()"

  it should "match if the PIC String value is equal" in {
    val pic1 = fromStringU(ValidPic1MaleBornIn1900s)
    val pic2 = fromStringU(ValidPic1MaleBornIn1900s)
    pic1 should equal(pic2)
  }

  it should "match if the PIC String is equivalent, but the control character is in a different case" in {
    val pic1 = fromStringU("010781-190A")
    val pic2 = fromStringU("010781-190a")
    pic1 should equal(pic2)
  }

  it should "match if the PIC String is equivalent, but the sign character is in a different case" in {
    val pic1 = fromStringU("170214A6228")
    val pic2 = fromStringU("170214a6228")
    pic1 should equal(pic2)
  }

  it should "not match if the PIC String is different" in {
    val pic1 = fromStringU(ValidPic1MaleBornIn1900s)
    val pic2 = fromStringU(ValidPic2FemaleBornIn1900s)
    pic1 should not equal pic2
  }

  it should "not match with an object of a different class" in {
    val pic = fromStringU(ValidPic1MaleBornIn1900s)
    val other = "foo!"
    pic should not equal other
  }

  behavior of "Pic.hashCode()"

  it should "be the same as the PIC String's hashCode" in {
    val hash1 = fromStringU(ValidPic1MaleBornIn1900s).hashCode()
    val hash2 = ValidPic1MaleBornIn1900s.hashCode
    hash1 should equal(hash2)
  }
}
