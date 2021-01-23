package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitE

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class TIOESpec extends Matchers with AnyFunSpecLike {

  describe("When working with Tio") {
    it("should work on an effect") {
      Runtime
        .run({
          for {
            some <- TIO.effect(333.toString)
          } yield some
        })
        .get shouldBe "333"
    }

    it("should aggregate multiple effects") {
      Runtime
        .run({
          for {
            some  <- TIO.effect(333.toString)
            other <- TIO.effect(" LET US PLAY".toLowerCase())
          } yield some + other
        })
        .get shouldBe "333 let us play"
    }

    it("should fail on a failure run") {
      intercept[Throwable] {
        Runtime
          .run({
            for {
              _ <- TIO.Fail(new Throwable("Boosh"))
            } yield ()
          })
          .get
      }
    }

    it("should recover with on a failure run") {
      Runtime
        .run({
          for {
            recoveredString <- TIO
              .effect(TIO.Fail(new Throwable("Boosh")).recover(e => TIO.effect("i win".toUpperCase)))
            stringElem <- recoveredString
          } yield stringElem
        })
        .get shouldBe "I WIN"
    }
  }

}
