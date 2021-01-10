package com.babs.denegee.api.broker.denegee_scope

import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class JobScopeInstanceSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using JobInstance") {
    it("should be a local scope") {
      val id = random[String]
      val jobName = random[String]
      val taskInstance = JobInstanceScope(id, jobName)
      taskInstance.isLocal shouldBe false
    }
    it("same taskInstance should be equal") {
      val id = random[String]
      val jobName = random[String]
      val taskInstance = JobInstanceScope(id, jobName)
      val taskInstance2 = JobInstanceScope(id, jobName)
      taskInstance shouldEqual taskInstance2
    }

    it("same taskInstance should have same hash code") {
      val id = random[String]
      val jobName = random[String]
      val taskInstance = JobInstanceScope(id, jobName)
      val taskInstance2 = JobInstanceScope(id, jobName)
      taskInstance.hashCode() shouldBe taskInstance2.hashCode()
    }

    it("same taskInstance should have same string value") {
      val id = random[String]
      val jobName = random[String]
      val taskInstance = JobInstanceScope(id, jobName)
      val taskInstance2 = JobInstanceScope(id, jobName)
      taskInstance.toString shouldBe taskInstance2.toString
    }

    it(
      "Should be able to access the task id from an instance of this TaskScope") {
      val id = random[String]
      val jobName = random[String]
      val jobInstance = JobInstanceScope(id, jobName)
      jobInstance.jobId shouldBe id
      jobInstance.jobName shouldBe jobName
    }
  }
}
