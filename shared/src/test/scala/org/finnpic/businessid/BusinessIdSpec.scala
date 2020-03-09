package org.finnpic.businessid

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class BusinessIdSpec extends AnyFlatSpecLike with Matchers {
  behavior of "BusinessId.fromString"

  it should "reject an empty string" in {
    BusinessId("") should be(Left("Invalid business id: ''. Business id should have 9 characters, but was 0 characters."))
  }

  it should "accept a valid business id" in {
    Seq("2933973-7", "1572860-0").foreach(s =>
      BusinessId(s) match {
        case Left(errorMsg: String) => fail(errorMsg)
        case Right(businessId) => businessId.value should be(s)
      })
  }

  it should "reject invalid business id" in { // TODO improve test name
    BusinessId("bcasdqr-w") should be(Left("Invalid business id: 'bcasdqr-w'. Business id should contain only digits and a dash."))
  }

  it should "reject business id with an invalid checksum character" in {
    BusinessId("2933973-8") should be(Left("Invalid business id: '2933973-8'. The checksum character '8' is wrong: it should be '7'."))
    BusinessId("2933973-6") should be(Left("Invalid business id: '2933973-6'. The checksum character '6' is wrong: it should be '7'."))
  }
}
