package com.babs.denegee.api.broker.denegee_scope

import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class TaskScopeInstanceSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using TaskInstance") {
    it("should be a local scope") {
      val id = random[String]
      val taskInstance = TaskScopeInstance(id)
      taskInstance.isLocal shouldBe true
    }
    it("same taskInstance should be equal") {
      val id = random[String]
      val taskInstance = TaskScopeInstance(id)
      val taskInstance2 = TaskScopeInstance(id)
      taskInstance shouldEqual taskInstance2
    }

    it("same taskInstance should have same hash code") {
      val id = random[String]
      val taskInstance = TaskScopeInstance(id)
      val taskInstance2 = TaskScopeInstance(id)
      taskInstance.hashCode() shouldBe taskInstance2.hashCode()
    }

    it("same taskInstance should have same string value") {
      val id = random[String]
      val taskInstance = TaskScopeInstance(id)
      val taskInstance2 = TaskScopeInstance(id)
      taskInstance.toString shouldBe taskInstance2.toString
    }

    it(
      "Should be able to access the task id from an instance of this TaskScope") {
      val id = random[String]
      val taskInstance = TaskScopeInstance(id)
      taskInstance.taskId shouldBe id
    }
  }
}
