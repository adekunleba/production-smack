package com.babs.denegee.api.ack

import com.babs.denegee.api.test.{HackableException, RandomGenerator}
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class AckableAckTestSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using Ackable") {
    it("should throw illegal exception when unknown ack strategy passed") {
      intercept[AckException](
        Ack("none").get
      )
    }

    it("should properly acknowledge if passed `ack` strategy") {
      Ack("ack").get shouldBe Acknowledged
    }

    it("should not acknowledge when null is passed") {
      Ack(null).get shouldBe NotAcknowledge
    }
  }

  describe("When using default Ack") {
    it("should not acknowledge on exception") {
      val ex = HackableException
      Ackable.nAck(ex) shouldBe NotAcknowledge
    }
  }

}
