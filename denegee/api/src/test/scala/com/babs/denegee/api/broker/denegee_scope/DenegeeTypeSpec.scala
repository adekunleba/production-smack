package com.babs.denegee.api.broker.denegee_scope

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class DenegeeTypeSpec extends Matchers with AnyFunSpecLike {

  describe("When using DegeneeetType") {

    it("should ensure that it evaluates if the type is local") {}

    it("should effectively return the root scope") {}
    it("should check default scope instance") {}

    it("should not give default scope instance when it is a task type") {}

    it("should give scope parents when parents is available") {}

    it("should not give scope parent when parents is unavailable") {}

    it("should check if parent is defined for the various scope types") {}

    it("should get parentscopes on being a ScopeType") {}
  }
}
