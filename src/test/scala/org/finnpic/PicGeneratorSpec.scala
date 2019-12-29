package org.finnpic

import java.time.LocalDate

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class PicGeneratorSpec extends AnyFlatSpecLike with Matchers {
  val testDate: LocalDate = LocalDate.of(2019, 12, 29)

  behavior of "PicGenerator.generateOne"

  it should "generate a valid PIC" in {
    implicit val seed: Long = 2
    val generatedPic: Pic = PicGenerator.generateOne()
    generatedPic should be(Pic.fromStringU("100140-3894"))
    generatedPic.ageInYearsAt(LocalDate.of(2019, 12, 29)) should be(79)
  }

  behavior of "PicGenerator.generateMany"

  it should "be able to generate 1000 valid PICs" in {
    implicit val seed: Long = 3
    val generatedPics: Seq[Pic] = PicGenerator.generateMany(1000)
    generatedPics.foreach(p => {
      // Check that this does not throw any RuntimeExceptions:
      // particularly not java.time.DateTimeException for impossible birth dates.
      p.ageInYearsNow()
      p.personIsOfFinnishLegalAgeNow()
    })
  }

  behavior of "PicGenerator.generateInfinite"

  it should "be able to generate 1000 valid PICs" in {
    implicit val seed: Long = 4
    val generatedPics: Seq[Pic] = PicGenerator.generateInfinite().take(1000)
    generatedPics.foreach(p => {
      // Check that this does not throw any RuntimeExceptions:
      // particularly not java.time.DateTimeException for impossible birth dates.
      p.ageInYearsNow()
      p.personIsOfFinnishLegalAgeNow()
    })
  }

  it should "be able to generate a PIC for a 5 years old female" in {
    implicit val seed: Long = 5
    val generatedPic: Pic = PicGenerator.generateInfinite().find(
      pic => pic.gender == Female && pic.ageInYearsAt(testDate) == 5
    ).get
    generatedPic.gender should be(Female)
    generatedPic.ageInYearsNow() should be(5)
    generatedPic.value should be("060714A7422")
  }

  it should "be able to generate a PIC for a 100 years old male" in {
    implicit val seed: Long = 5
    val generatedPic: Pic = PicGenerator.generateInfinite().find(
      pic => pic.gender == Male && pic.ageInYearsAt(testDate) == 100
    ).get
    generatedPic.gender should be(Male)
    generatedPic.ageInYearsNow() should be(100)
    generatedPic.value should be("181219-4634")
  }

  behavior of "PicGenerator.generateOneWithSpecification"

  it should "generate a PIC of the given specification" in {
    implicit val seed: Long = 6
    val generatedPic: Pic = PicGenerator.generateOneWithSpecification(
      pic => pic.ageInYearsAt(testDate) == 42 && pic.gender == Male
    )
    generatedPic.value should be("071177-0296")
  }

  behavior of "PicGenerator.generateManyWithSpecification"

  it should "generate 5 PICs of the given specification" in {
    implicit val seed: Long = 7
    val generatedPics: Seq[Pic] = PicGenerator.generateManyWithSpecification(
      pic => pic.ageInYearsAt(testDate) == 42 && pic.gender == Male
    )(5)
    generatedPics.length should be(5)
    generatedPics(0).value should be("260677-6736")
    generatedPics(1).value should be("240877-307P")
    generatedPics(2).value should be("041077-873N")
    generatedPics(3).value should be("131177-847E")
    generatedPics(4).value should be("300877-2591")
  }
}
