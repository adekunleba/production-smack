package com.babs.denegee.api.configuration

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

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

    it("State should update on more than one config property addition ") {
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      val propsState = PropsStateImm()
      val propState2 = propsState.addAllP(config2)
      propState2.specialProperties.isEmpty shouldBe false
      val config1 = ConfigProperties[String, String]()
      config1.setProperty("Maker", "juli")
      val propState3 = propState2.addAllP(config1)
      propState3.specialProperties.isEmpty shouldBe false
      propState3.specialProperties shouldBe ConfigProperties(
        mutable.HashMap("John" -> "Mak", "Maker" -> "juli")
      )
    }

    it("Should update properties from another state ") {
      val config1 = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val config2 = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propsState1 = PropsStateImm().addAllP(config1)
      val propState2 = PropsStateImm().addAllP(config2)
      val propsAdd = propsState1.addAll(propState2)
      propsAdd.specialProperties.isEmpty shouldBe false
      propsAdd.specialProperties shouldBe ConfigProperties(
        mutable.HashMap("John" -> "Sure", "Salin" -> "Mike")
      )
    }

    it("Should get all properties") {
      val specProps = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val commProps = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propState1 = PropsStateImm(specProps, commProps)
      propState1.getProperties.isEmpty shouldBe false
      propState1.getProperties shouldBe ConfigProperties(
        specProps.getprops ++ commProps.getprops
      )
    }

    it("Should add properties if not exists") {
      val specProps = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val commProps = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propsState1 = PropsStateImm(specProps, commProps)

      val configToAdd =
        ConfigProperties(mutable.HashMap("John" -> "Sure", "Lolu" -> "shear"))
      val propsState2 = propsState1.addAllIfNotExists(configToAdd)
      propsState2.getProperties.isEmpty shouldBe false
      propsState2.getProperties shouldBe
        ConfigProperties(
          specProps.getprops ++ commProps.getprops ++ configToAdd.getprops)
    }

    it("Should override properties") {
      val specProps = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val commProps = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propsState1 = PropsStateImm(commProps, specProps)

      val configToAdd =
        ConfigProperties(mutable.HashMap("John" -> "Mandy", "Lolu" -> "shear"))
      val propsState2 = propsState1.overrideWith(configToAdd)
      propsState2.getProperties shouldBe ConfigProperties(
        mutable.HashMap("John" -> "Mandy", "Salin" -> "Mike")
      )
    }

  }

}
