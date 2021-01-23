package com.babs.denegee.api.broker.denegee_scope

import com.babs.denegee.api.broker.ScopeInstanceException
import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class DenegeeScopesTest
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using DenegreeScopes") {
    it("should get scope id") {
      val containerId = random[String]
      val containerScope =
        DenegeeScopeInstance(DenegeeScopeTypesV2.Container, containerId)
      containerScope.scopeId shouldBe containerId

    }

    it("should throw ScopeInstanceException when creating scope of Job ") {
      val jobId = random[String]
      intercept[ScopeInstanceException](
        DenegeeScopeInstance(DenegeeScopeTypesV2.Job, jobId)
      )
    }
    it("should throw ScopeInstanceException when creating Tasks") {
      val taskId = random[String]
      intercept[ScopeInstanceException](
        DenegeeScopeInstance(DenegeeScopeTypesV2.Task, taskId)
      )
    }

    it(
      "should return default instance informations when working with Containers") {
      val containerId = random[String]
      val containerScope =
        DenegeeScopeInstance(DenegeeScopeTypesV2.Container, containerId)
      containerScope.defaultScopeInstance.isDefined shouldBe true
      containerScope.defaultScopeInstance.get shouldBe containerScope
      containerScope.parentScopes shouldBe DenegeeScopeTypesV2.Job.scopeParent.get
      containerScope.rootScope shouldBe DenegeeScopeTypesV2.Global
      containerScope.isLocal shouldBe true
    }

    it("should return default instance informations when working with Instance") {
      val instanceId = random[String]
      val instanceScope =
        DenegeeScopeInstance(DenegeeScopeTypesV2.Instance, instanceId)
      instanceScope.defaultScopeInstance.isDefined shouldBe true
      instanceScope.defaultScopeInstance.get shouldBe instanceScope
      instanceScope.parentScopes.isEmpty shouldBe false
      instanceScope.parentScopes shouldBe DenegeeScopeTypesV2.Instance.scopeParent.get
      instanceScope.rootScope shouldBe DenegeeScopeTypesV2.Global
      instanceScope.isLocal shouldBe false
    }

    it("should return default instance informations when working with Global") {
      val globalId = random[String]
      val globalScope =
        DenegeeScopeInstance(DenegeeScopeTypesV2.Global, globalId)
      globalScope.defaultScopeInstance.isDefined shouldBe true
      globalScope.defaultScopeInstance.get shouldBe globalScope
      globalScope.parentScopes.isEmpty shouldBe true
      globalScope.rootScope shouldBe DenegeeScopeTypesV2.Global
      globalScope.isLocal shouldBe false
    }
  }
}
