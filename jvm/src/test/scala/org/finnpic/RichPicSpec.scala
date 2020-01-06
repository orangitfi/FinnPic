package org.finnpic

import java.time.{Clock, Instant, LocalDate, ZoneId}

import org.finnpic.Pic.fromStringU
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class RichPicSpec extends AnyFlatSpecLike with Matchers {
  val validPic1MaleBornIn1900s = "070377-281V"
  val validPic2FemaleBornIn1900s = "211281-862X"
  val validPic3FemaleBornIn2000s = "211114A664E"
  val validPic4MaleBornIn1800s = "210777+9955"

  val validPics: List[String] = List(validPic1MaleBornIn1900s, validPic2FemaleBornIn1900s, validPic3FemaleBornIn2000s, validPic4MaleBornIn1800s)

  val helsinkiTimeZone: ZoneId = ZoneId.of("Europe/Helsinki")

  behavior of "class RichPic"

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

  it should "know if the person is of Finnish legal age now" in {
    implicit val at29081995: Clock = Clock.fixed(Instant.ofEpochSecond(809715600), ZoneId.of("Europe/Helsinki"))
    fromStringU(validPic1MaleBornIn1900s).personIsOfFinnishLegalAgeNow() should be(true)
    fromStringU(validPic2FemaleBornIn1900s).personIsOfFinnishLegalAgeNow() should be(false)
    fromStringU(validPic3FemaleBornIn2000s).personIsOfFinnishLegalAgeNow() should be(false)
    fromStringU(validPic4MaleBornIn1800s).personIsOfFinnishLegalAgeNow() should be(true)
  }
}
