package org.finnpic

// Note: Commented out until scalacheck and scalatest/scalactic support the same version of Scala.js.
// - vpeurala, 25.12.2019
/*
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
*/
