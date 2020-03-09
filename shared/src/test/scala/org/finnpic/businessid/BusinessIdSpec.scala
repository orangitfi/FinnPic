package org.finnpic.businessid

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class BusinessIdSpec extends AnyFlatSpecLike with Matchers {
  behavior of "BusinessId.fromString"

  it should "reject an empty string" in {
    BusinessId("") should be(Left("Invalid business id: ''. Business id should have 9 characters, but was 0 characters."))
  }
}
