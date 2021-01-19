package com.babs.denegee.api.ack

import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class HeirachichalAckTestSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using HeirachicalAck") {
    it("should not acknowledge parents on start and when ack is closed") {
      val parentHacks: Seq[Ackable] = Seq(BasicAckable())
      val hack = HeirachichalAckable(parentHacks)
      val childAck1 = hack.createChildAck()
      val childAck2 = hack.createChildAck()

      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0

      hack.close()
      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0
    }

    it("should throw Ackexception when creating new child and ack is closed") {
      val parentHacks: Seq[Ackable] = Seq(BasicAckable())
      val hack = HeirachichalAckable(parentHacks)
      val childAck1 = hack.createChildAck()
      val childAck2 = hack.createChildAck()
      hack.close()
      intercept[AckException](
        hack.createChildAck()
      )
    }

    it("should not acknowledge parents when only one child is acknowledge") {
      val parentHacks: Seq[Ackable] = Seq(BasicAckable())
      val hack = HeirachichalAckable(parentHacks)
      val childAck1 = hack.createChildAck()
      val childAck2 = hack.createChildAck()
      hack.close()
      childAck1.ack
      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0
    }

    it("should acknowledge parents when all child is acknowledge") {
      val parentHacks: Seq[Ackable] = Seq(BasicAckable())
      val hack = HeirachichalAckable(parentHacks)
      val childAck1 = hack.createChildAck()
      val childAck2 = hack.createChildAck()
      hack.close()
      childAck1.ack
      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0

      childAck2.ack
      hack.parents.map(_.getAcknowledged).sum shouldBe 1
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0
    }

    it(
      "should acknowledge not acknowledge parents when all child is not acknowledged") {
      val parentHacks: Seq[Ackable] = Seq(BasicAckable())
      val hack = HeirachichalAckable(parentHacks)
      val childAck1 = hack.createChildAck()
      val childAck2 = hack.createChildAck()
      hack.close()
      childAck1.nAck
      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 0

      childAck2.nAck
      hack.parents.map(_.getAcknowledged).sum shouldBe 0
      hack.parents.map(_.getNAcknowledged).sum shouldBe 1
    }

  }
}
