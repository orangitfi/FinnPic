package org.finnpic

import java.time.LocalDate

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class PicGeneratorSpec extends AnyFlatSpecLike with Matchers {
  behavior of "PicGenerator.generateOne"

  it should "generate a valid PIC" in {
    implicit val seed: Long = 2
    val generatedPic: Pic = PicGenerator.generateOne()
    generatedPic should be(Pic.fromStringU("100140-3894"))
    generatedPic.ageInYearsAt(LocalDate.of(2019, 12, 29)) should be(79)
  }

  behavior of "PicGenerator.generateMany"

  it should "be able to generate 1000 valid PICs" in {
    val generatedPics = PicGenerator.generateMany(1000)
    generatedPics.foreach(p => {
      // Check that this does not throw any RuntimeExceptions:
      // particularly not java.time.DateTimeException for impossible birth dates.
      p.ageInYearsNow()
      p.personIsOfFinnishLegalAgeNow()
    })
  }
}
