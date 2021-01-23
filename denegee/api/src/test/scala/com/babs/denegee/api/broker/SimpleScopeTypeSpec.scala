package com.babs.denegee.api.broker

import com.babs.denegee.api.broker.iface.SimpleScopeInstance
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class SimpleScopeTypeSpec extends Matchers with AnyFunSpecLike {

  val localScope: SimpleScopeType = SimpleScopeType.Local
  val globalScope: SimpleScopeType = SimpleScopeType.Global

  describe("When using SimpleScopeType") {

    it("should ensure that it evaluates if the type is local") {
      assert(
        Seq(localScope)
          .map(_.isLocal)
          .reduce(_ && _)
      )
    }

    it("should effectively return the root scope") {
      val rootScope = Seq(localScope)
        .map(_.handlerRootScope)
        .toSet

      rootScope.size shouldBe 1
      rootScope.head shouldBe SimpleScopeType.Global
    }
    it("should check default scope instance") {
      val scopes =
        Seq(localScope)
          .flatMap(_.defaultScopeInstance)
      scopes
        .map(_.isInstanceOf[SimpleScopeInstance[SimpleScopeType]])
        .reduce(_ && _) shouldBe true
    }
  }
}
