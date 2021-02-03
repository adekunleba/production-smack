package com.babs.denegee.api.configuration

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class SourceStateTest extends Matchers with AnyFunSpecLike {

  describe("When using SourceState") {
    it("should add configuration when instantiated with config") {
      val specProps = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val commProps = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propsState1: PropsStateImm = PropsStateImm(commProps, specProps)
      val sourceState = SourceState(propsState1)
      sourceState.stateManager.getProperties shouldBe ConfigProperties(
        mutable.HashMap("John" -> "Sure", "Salin" -> "Mike")
      )
    }

    it("should instantiate with previous work unit state") {
      val specProps = ConfigProperties(mutable.HashMap("John" -> "Sure"))
      val commProps = ConfigProperties(mutable.HashMap("Salin" -> "Mike"))
      val propsState1: PropsStateImm = PropsStateImm(commProps, specProps)
      val existingSourceState = SourceState[WorkUnitState](propsState1)

      // Well WorkUnitState requires other implementations - Hence we should do that.

    }
  }
}
