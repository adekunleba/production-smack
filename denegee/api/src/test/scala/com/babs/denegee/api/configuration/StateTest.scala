package com.babs.denegee.api.configuration

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class StateTest extends Matchers with AnyFunSpecLike {

  /* Test implemented to run concurrently, can easily use
   * Scala future for this
   */

  describe("When using ProsStet") {
    it("should create an empty state with empty property") {
      val propsState = PropsState()
      propsState.commonProperties.isEmpty shouldBe true
      propsState.specialProperties.isEmpty shouldBe true
    }

    it("Should not clear existing state on update state config") {}

    it("Should add properties to the state ") {
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      val propsState = PropsState()
      val propState2 = propsState.addAllP(config2)
      propsState.specialProperties.isEmpty shouldBe false
      propsState.specialProperties shouldBe config2
      propsState shouldBe propState2
    }
  }

}
