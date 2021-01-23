package com.babs.denegee.api.broker.denegee_scope

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class DenegeeTypeSpec extends Matchers with AnyFunSpecLike {

  val localContainerType: DenegeeScopeTypesV2 =
    DenegeeScopeTypesV2.Container
  val localTaskType: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Task
  val localMultiTask: DenegeeScopeTypesV2 =
    DenegeeScopeTypesV2.MultiTaskAttempt

  val jobScope: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Job
  val instanceScpoe: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Instance

  describe("When using DegeneetType") {

    it("should ensure that it evaluates if the type is local") {

      assert(
        Seq(localContainerType, localTaskType, localMultiTask)
          .map(_.isLocal)
          .reduce(_ && _)
      )
    }

    it("should effectively return the root scope") {
      val rootScope = Seq(localContainerType, localTaskType, localMultiTask)
        .map(_.handlerRootScope)
        .toSet

      rootScope.size shouldBe 1
      rootScope.head shouldBe DenegeeScopeTypesV2.Global

    }
    it("should check default scope instance") {
      val scopes =
        Seq(localContainerType, localMultiTask, instanceScpoe)
          .flatMap(_.defaultScopeInstance)
      scopes
        .map(_.isInstanceOf[DenegeeScopeInstance])
        .reduce(_ && _) shouldBe true
    }

    it("should not give default scope instance when it is a task type") {
      val scopes =
        Seq(jobScope, localTaskType)
          .flatMap(_.defaultScopeInstance)
      scopes.isEmpty shouldBe true
    }

    it("should give scope parents when parents is available") {
      val scopes = Seq(localContainerType,
                       localMultiTask,
                       instanceScpoe,
                       jobScope,
                       localTaskType)
      scopes.map(_.parentScopes().nonEmpty).reduce(_ && _) shouldBe true
    }

    it("should not give scope parent when parents is unavailable") {

      val scopes: DenegeeScopeTypesV2 = DenegeeScopeTypesV2.Global
      scopes.parentScopes().isEmpty shouldBe true
    }

  }
}
