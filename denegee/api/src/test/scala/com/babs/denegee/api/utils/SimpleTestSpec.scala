package com.babs.denegee.api.utils

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class SimpleTestSpec extends Matchers with AnyFunSpecLike {

  describe("Should run basic hello world test") {
    it("should say hello in") {
      val string = "Hello World!"
      string shouldEqual "Hello World!"
    }
  }
}
