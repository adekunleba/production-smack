package com.babs.denegee.api.ack

import com.babs.denegee.api.test.HackableException
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class BasicAckingTestSpec extends Matchers with AnyFunSpecLike {

  describe("When using BasicAcking") {

    it("should be empty if no acknowledgment or non acknowledgement") {
      val basicAcking = BasicAcking()
      basicAcking.getNAcknowledged shouldBe 0
      basicAcking.getAcknowledged shouldBe 0
    }
    it("should increment acknowledged store") {

      val basicAck = BasicAcking()
      // One ack
      basicAck.ack
      basicAck.getAcknowledged shouldBe 1
      basicAck.ack
      basicAck.getAcknowledged shouldBe 2
    }
    it("should increment nonacknowledge store") {
      val basicAck = BasicAcking()
      // One nack
      basicAck.nAck
      basicAck.getNAcknowledged shouldBe 1
      basicAck.nAck
      basicAck.getNAcknowledged shouldBe 2
      basicAck.nAck
      basicAck.nAck
      basicAck.nAck
      basicAck.getNAcknowledged shouldBe 5
    }
    it("should increment nonacknowledge store when throwable passed") {
      val basicAck = BasicAcking()
      val exception = HackableException
      // One ack
      basicAck.nAck
      basicAck.getNAcknowledged shouldBe 1
      basicAck.nAck
      basicAck.getNAcknowledged shouldBe 2
      basicAck.nAck
      basicAck.nAck(exception)
      basicAck.nAck(exception)
      basicAck.getNAcknowledged shouldBe 5
    }

    it("should increment acknowledge when an acknowledge strategy passed") {

      val basicAck = BasicAcking()
      // One ack
      basicAck.ackWithStrategy("ack")
      basicAck.getAcknowledged shouldBe 1
      basicAck.ackWithStrategy("ack")
      basicAck.getAcknowledged shouldBe 2
      basicAck.ackWithStrategy(null)
      basicAck.getNAcknowledged shouldBe 1
      basicAck.ackWithStrategy("Unknown")
      basicAck.getNAcknowledged shouldBe 2
    }
  }
}
