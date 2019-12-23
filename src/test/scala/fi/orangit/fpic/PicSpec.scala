package fi.orangit.fpic

import java.time.{Clock, Instant, LocalDate, ZoneId}

import fi.orangit.fpic.Pic.{fromString, fromStringU}
import org.scalatest.{FlatSpecLike, Matchers}

import scala.util.{Failure, Success, Try}

/**
 * See https://vrk.fi/en/personal-identity-code1 for specs.
 */
class PicSpec extends FlatSpecLike with Matchers {
  val validPic1MaleBornIn1900s = "290877-1639"
  val validPic2FemaleBornIn1900s = "010781-190A"
  val validPic3FemaleBornIn2000s = "170214A6228"
  val validPic4MaleBornIn1800s = "290877+1639"

  val validPics: List[String] = List(validPic1MaleBornIn1900s, validPic2FemaleBornIn1900s, validPic3FemaleBornIn2000s, validPic4MaleBornIn1800s)

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
    val t: Try[Pic] = Try(fromStringU("290877-1640"))
    t match {
      case Failure(err) => {
        err.getMessage should be("Invalid PIC: '290877-1640'. The control character ('0') is wrong: it should be 'A'.")
      }
      case Success(_) => fail("An IllegalArgumentException should have been thrown, but was not.")
    }
  }

  behavior of "class Pic"

  it should "know the supposed gender of the person" in {
    fromStringU(validPic1MaleBornIn1900s).gender should be(Male)
    fromStringU(validPic2FemaleBornIn1900s).gender should be(Female)
    fromStringU(validPic3FemaleBornIn2000s).gender should be(Female)
    fromStringU(validPic4MaleBornIn1800s).gender should be(Male)
  }

  it should "know the birth year of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthYear should be(1977)
    fromStringU(validPic2FemaleBornIn1900s).birthYear should be(1981)
    fromStringU(validPic3FemaleBornIn2000s).birthYear should be(2014)
    fromStringU(validPic4MaleBornIn1800s).birthYear should be(1877)
  }

  it should "know the birth month of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthMonth should be(8)
    fromStringU(validPic2FemaleBornIn1900s).birthMonth should be(7)
    fromStringU(validPic3FemaleBornIn2000s).birthMonth should be(2)
    fromStringU(validPic4MaleBornIn1800s).birthMonth should be(8)
  }

  it should "know the birth day (of month) of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthDay should be(29)
    fromStringU(validPic2FemaleBornIn1900s).birthDay should be(1)
    fromStringU(validPic3FemaleBornIn2000s).birthDay should be(17)
    fromStringU(validPic4MaleBornIn1800s).birthDay should be(29)
  }

  it should "know the birth date of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthDate should be(LocalDate.of(1977, 8, 29))
    fromStringU(validPic2FemaleBornIn1900s).birthDate should be(LocalDate.of(1981, 7, 1))
    fromStringU(validPic3FemaleBornIn2000s).birthDate should be(LocalDate.of(2014, 2, 17))
    fromStringU(validPic4MaleBornIn1800s).birthDate should be(LocalDate.of(1877, 8, 29))
  }

  it should "know the age of the person in question, on the day before birthday" in {
    implicit val at28081995: Clock = Clock.fixed(Instant.ofEpochSecond(809629200), ZoneId.of("Europe/Helsinki"))
    fromStringU(validPic1MaleBornIn1900s).ageInYearsNow() should be(17)
    fromStringU(validPic2FemaleBornIn1900s).ageInYearsNow() should be(14)
    fromStringU(validPic3FemaleBornIn2000s).ageInYearsNow() should be(-18)
    fromStringU(validPic4MaleBornIn1800s).ageInYearsNow() should be(117)
  }

  it should "know the age of the person in question, on birthday" in {
    implicit val at29081995: Clock = Clock.fixed(Instant.ofEpochSecond(809715600), ZoneId.of("Europe/Helsinki"))
    fromStringU(validPic1MaleBornIn1900s).ageInYearsNow() should be(18)
    fromStringU(validPic2FemaleBornIn1900s).ageInYearsNow() should be(14)
    fromStringU(validPic3FemaleBornIn2000s).ageInYearsNow() should be(-18)
    fromStringU(validPic4MaleBornIn1800s).ageInYearsNow() should be(118)
  }

  it should "know if the person if os Finnish legal age at some point in time, on the day before birthday" in {
    val at29081995: LocalDate = LocalDate.of(1995, 8, 28)
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeAt(at29081995) should be(false)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeAt(at29081995) should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeAt(at29081995) should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeAt(at29081995) should be(true)
  }

  it should "know if the person if os Finnish legal age at some point in time, on birthday" in {
    val at29081995: LocalDate = LocalDate.of(1995, 8, 29)
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeAt(at29081995) should be(true)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeAt(at29081995) should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeAt(at29081995) should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeAt(at29081995) should be(true)
  }

  it should "know if the person is of Finnish legal age now" in {
    implicit val at29081995: Clock = Clock.fixed(Instant.ofEpochSecond(809715600), ZoneId.of("Europe/Helsinki"))
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeNow() should be(true)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeNow() should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeNow() should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeNow() should be(true)
  }

  behavior of "Pic.toString()"

  it should "just show the original pic (the String value) without any decoration" in {
    fromStringU(validPic1MaleBornIn1900s).toString should be(validPic1MaleBornIn1900s)
    fromStringU(validPic2FemaleBornIn1900s).toString should be(validPic2FemaleBornIn1900s)
    fromStringU(validPic3FemaleBornIn2000s).toString should be(validPic3FemaleBornIn2000s)
    fromStringU(validPic4MaleBornIn1800s).toString should be(validPic4MaleBornIn1800s)
  }

  behavior of "Pic.equals()"

  it should "match if the PIC String value is equal" in {
    val pic1 = fromStringU(validPic1MaleBornIn1900s)
    val pic2 = fromStringU(validPic1MaleBornIn1900s)
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
    val pic1 = fromStringU(validPic1MaleBornIn1900s)
    val pic2 = fromStringU(validPic2FemaleBornIn1900s)
    pic1 should not equal pic2
  }

  it should "not match with an object of a different class" in {
    val pic = fromStringU(validPic1MaleBornIn1900s)
    val other = "foo!"
    pic should not equal other
  }

  behavior of "Pic.hashCode()"

  it should "be the same as the PIC String's hashCode" in {
    val hash1 = fromStringU(validPic1MaleBornIn1900s).hashCode()
    val hash2 = validPic1MaleBornIn1900s.hashCode
    hash1 should equal(hash2)
  }
}
