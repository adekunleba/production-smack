package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitA

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class TIOASpec extends Matchers with AnyFunSpecLike {

  describe("When working with Tio") {
    it("should work on an effect") {
      Runtime.run({
        for {
          some <- TIO.effect(333.toString)
        } yield some
      }) shouldBe "333"
    }

    it("should aggregate multiple effects") {
      Runtime.run({
        for {
          some  <- TIO.effect(333.toString)
          other <- TIO.effect(" LET US PLAY".toLowerCase())
        } yield some + other
      }) shouldBe "333 let us play"
    }
  }

}
