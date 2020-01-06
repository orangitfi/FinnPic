package org.finnpic

import java.time.{Clock, Instant, LocalDate, ZoneId}

import org.finnpic.Pic.fromStringU
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.util.{Failure, Success, Try}

/**
 * See https://vrk.fi/en/personal-identity-code1 for specs.
 */
class PicSpec extends AnyFlatSpecLike with Matchers {
  val validPic1MaleBornIn1900s = "070377-281V"
  val validPic2FemaleBornIn1900s = "211281-862X"
  val validPic3FemaleBornIn2000s = "211114A664E"
  val validPic4MaleBornIn1800s = "210777+9955"

  val validPics: List[String] = List(validPic1MaleBornIn1900s, validPic2FemaleBornIn1900s, validPic3FemaleBornIn2000s, validPic4MaleBornIn1800s)

  val helsinkiTimeZone: ZoneId = ZoneId.of("Europe/Helsinki")

  behavior of "object Pic, method fromString"

  it should "reject an empty string" in {
    Pic("") should be(Left("Invalid PIC: ''. PIC should have 11 characters, but was 0 characters."))
  }

  it should "reject a PIC with non-numeric character in the first 6 places" in {
    Pic("07037X-281V") should be(Left("Invalid PIC: '07037X-281V'. The first six characters have to be numeric, but they were: '07037X'."))
  }

  it should "reject a PIC where the delimiter char is something other than +, -, or A" in {
    Pic("070377X281V") should be(Left("Invalid PIC: '070377X281V'. The sign (7th character) must be +, - or A, now it was: 'X'."))
  }

  it should "reject a PIC where the individual number is not numeric" in {
    Pic("070377-28AV") should be(Left("Invalid PIC: '070377-28AV'. The individual number (characters 8-10) must be numeric, now it was: '28A'."))
  }

  it should "reject a PIC the control character is wrong" in {
    Pic("070377-2818") should be(Left("Invalid PIC: '070377-2818'. The control character ('8') is wrong: it should be 'V'."))
  }

  it should "reject a PIC where the day part of the birth date part is something else than 1-31" in {
    Pic("000877-163K") should be(Left("Invalid PIC: '000877-163K'. The day part of the birth date is wrong: it should be 01-31, but was: '00'."))
    Pic("320877-163K") should be(Left("Invalid PIC: '320877-163K'. The day part of the birth date is wrong: it should be 01-31, but was: '32'."))
  }

  it should "reject a PIC where the month part of the birth date part is something else than 1-12" in {
    Pic("070077-281V") should be(Left("Invalid PIC: '070077-281V'. The month part of the birth date is wrong: it should be 01-12, but was: '00'."))
    Pic("071377-281V") should be(Left("Invalid PIC: '071377-281V'. The month part of the birth date is wrong: it should be 01-12, but was: '13'."))
  }

  it should "reject a PIC where the birth date is impossible" in {
    // There are never 31 days in February, so 31.2. is illegal.
    Pic("310277-163R") should be(Left("Invalid PIC: '310277-163R'. The birth date is impossible, this day does not exist on the calendar: '310277'."))
    // Year 1980 was a leap year, so 29.2. is legal.
    Pic("290280-123X") match {
      case Left(errMsg) => fail(s"Creation of a legal PIC on a leap day failed: ${errMsg}")
      case Right(_) => // pass
    }
    // Year 1981 was not a leap year, so 29.2. is illegal.
    Pic("290281-1236") should be(Left("Invalid PIC: '290281-1236'. The birth date is impossible, this day does not exist on the calendar: '290281'."))
  }

  it should "accept a valid PIC" in {
    validPics.foreach(s =>
      Pic(s) match {
        case Left(errorMsg: String) => fail(errorMsg)
        case Right(pic) => pic.value should be(s)
      })
  }

  it should "uppercase all characters to achieve a canonical representation" in {
    fromStringU("211281-862x").value should be("211281-862X")
    fromStringU("211114a664e").value should be("211114A664E")
  }

  behavior of "object Pic, method fromStringUnsafe / fromStringU"

  it should "throw an IllegalArgumentException if the input is not a valid PIC" in {
    val t: Try[Pic] = Try(fromStringU("070377-2819"))
    t match {
      case Failure(err) => {
        err.getMessage should be("Invalid PIC: '070377-2819'. The control character ('9') is wrong: it should be 'V'.")
      }
      case Success(_) => fail("An IllegalArgumentException should have been thrown, but was not.")
    }
  }

  it should "accept a valid PIC" in {
    fromStringU("230625A323J") // pass
    fromStringU("120128A116H") // pass
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
    fromStringU(validPic1MaleBornIn1900s).birthMonth should be(3)
    fromStringU(validPic2FemaleBornIn1900s).birthMonth should be(12)
    fromStringU(validPic3FemaleBornIn2000s).birthMonth should be(11)
    fromStringU(validPic4MaleBornIn1800s).birthMonth should be(7)
  }

  it should "know the birth day (of month) of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthDay should be(7)
    fromStringU(validPic2FemaleBornIn1900s).birthDay should be(21)
    fromStringU(validPic3FemaleBornIn2000s).birthDay should be(21)
    fromStringU(validPic4MaleBornIn1800s).birthDay should be(21)
  }

  it should "know the birth date of the person" in {
    fromStringU(validPic1MaleBornIn1900s).birthDate should be(LocalDate.of(1977, 3, 7))
    fromStringU(validPic2FemaleBornIn1900s).birthDate should be(LocalDate.of(1981, 12, 21))
    fromStringU(validPic3FemaleBornIn2000s).birthDate should be(LocalDate.of(2014, 11, 21))
    fromStringU(validPic4MaleBornIn1800s).birthDate should be(LocalDate.of(1877, 7, 21))
  }

  it should "know the age of the person in question, on the day before birthday" in {
    val dayBefore18thBirthday = LocalDate.of(1995, 3, 6)
    implicit val atDayBefore18thBirthday: Clock =
      Clock.fixed(dayBefore18thBirthday.atTime(17, 0).
        toInstant(
          helsinkiTimeZone.getRules.getOffset(dayBefore18thBirthday.atTime(17, 0))
        ),
        helsinkiTimeZone
      )
    fromStringU(validPic1MaleBornIn1900s).ageInYearsNow() should be(17)
    fromStringU(validPic2FemaleBornIn1900s).ageInYearsNow() should be(13)
    fromStringU(validPic3FemaleBornIn2000s).ageInYearsNow() should be(-19)
    fromStringU(validPic4MaleBornIn1800s).ageInYearsNow() should be(117)
  }

  it should "know the age of the person in question, on birthday" in {
    val dayOf18thBirthday = LocalDate.of(1995, 3, 7)
    implicit val atDayOf18thBirthday: Clock = Clock.fixed(dayOf18thBirthday.atTime(0, 0).
      toInstant(
        helsinkiTimeZone.getRules.getOffset(dayOf18thBirthday.atTime(8, 0))
      ),
      helsinkiTimeZone)
    fromStringU(validPic1MaleBornIn1900s).ageInYearsNow() should be(18)
    fromStringU(validPic2FemaleBornIn1900s).ageInYearsNow() should be(13)
    fromStringU(validPic3FemaleBornIn2000s).ageInYearsNow() should be(-19)
    fromStringU(validPic4MaleBornIn1800s).ageInYearsNow() should be(117)
  }

  it should "know if the person if os Finnish legal age at some point in time, on the day before birthday" in {
    val atDayBefore18thBirthday: LocalDate = LocalDate.of(1995, 3, 6)
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeAt(atDayBefore18thBirthday) should be(false)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeAt(atDayBefore18thBirthday) should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeAt(atDayBefore18thBirthday) should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeAt(atDayBefore18thBirthday) should be(true)
  }

  it should "know if the person if os Finnish legal age at some point in time, on birthday" in {
    val atDayOf18thBirthday: LocalDate = LocalDate.of(1995, 3, 7)
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeAt(atDayOf18thBirthday) should be(true)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeAt(atDayOf18thBirthday) should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeAt(atDayOf18thBirthday) should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeAt(atDayOf18thBirthday) should be(true)
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
    val pic1 = fromStringU("211281-862X")
    val pic2 = fromStringU("211281-862x")
    pic1 should equal(pic2)
  }

  it should "match if the PIC String is equivalent, but the sign character is in a different case" in {
    val pic1 = fromStringU("211114A664E")
    val pic2 = fromStringU("211114a664E")
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
