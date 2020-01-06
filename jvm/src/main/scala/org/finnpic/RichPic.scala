package org.finnpic

import java.time.{Clock, LocalDate, Period}

case class RichPic(pic: Pic) {
  def ageNow()(implicit clock: Clock = Clock.systemDefaultZone()): Period = {
    pic.ageAt(LocalDate.now(clock))
  }

  def ageInYearsNow()(implicit clock: Clock = Clock.systemDefaultZone()): Int = {
    ageNow().getYears
  }

  def personIsOfFinnishLegalAgeNow()(implicit clock: Clock = Clock.systemDefaultZone()): Boolean = {
    ageInYearsNow() >= Pic.finnishLegalAge
  }
}
