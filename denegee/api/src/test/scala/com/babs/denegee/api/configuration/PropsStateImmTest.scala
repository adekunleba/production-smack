package com.babs.denegee.api.configuration

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class PropsStateImmTest extends Matchers with AnyFunSpecLike {

  /* Test implemented to run concurrently, can easily use
   * Scala future for this
   */

  describe("When using ProsStet") {
    it("should create an empty state with empty property") {
      val propsState = PropsStateImm()
      propsState.commonProperties.isEmpty shouldBe true
      propsState.specialProperties.isEmpty shouldBe true
    }

    //    it("Should not clear existing state on update state config") {}
    //
    it("Should add properties to the state ") {
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      val propsState = PropsStateImm()
      val propState2 = propsState.addAllP(config2)
      propState2.specialProperties.isEmpty shouldBe false
      propState2.specialProperties shouldBe config2
    }

    it("State one should not be state 2 on update ") {
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      val propsState = PropsStateImm()
      val propState2 = propsState.addAllP(config2)
      propState2.specialProperties.isEmpty shouldBe false
      propsState should not be propState2
    }
  }

}
