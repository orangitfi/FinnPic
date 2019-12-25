package org.finnpic

import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object PicPropertySpec extends Properties("Pic.fromString") {
  property("does not throw runtime exceptions") = forAll { (s: String) =>
    val e: Either[String, Pic] = Pic.fromString(s)
    e match {
      case Left(errMsg) => true
      case Right(pic) => pic.value == s
    }
  }
}
