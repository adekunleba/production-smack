package com.babs.denegee.api.utils

import com.babs.denegee.reflect.Alias
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class ClassAliasResolverSpec extends Matchers with AnyFunSpecLike {

  val resolver: ClassAliasResolver[IDummyAliasTest] =
    ClassAliasResolver(classOf[IDummyAliasTest])

  describe("When using ClassAliasResolver ") {

    it("Should correctly get list to scan from config") {
      resolver.packagesToScan shouldBe List("com.babs.denegee",
                                            "denegee",
                                            "com.linkedin.gobblin")
    }
    it("should validate that cache map created for annotate subclass") {
      resolver.packageMap.isEmpty shouldBe false
    }
    it(
      "Should resolve the class name if its aliased return name if not alias present") {
      resolver.resolve("com.alias") shouldBe DummAliasTest.getClass.getName
      resolver.resolve("abc") shouldBe "abc"
    }

    it("Should instantiate class by name") {
      resolver.resolveClass("com.alias").get shouldBe DummAliasTest.getClass
      resolver
        .resolveClass(DummAliasTest.getClass.getName)
        .get shouldBe DummAliasTest.getClass
    }

    it("Should return none if class not found") {
      resolver.resolveClass("abc") shouldBe None
    }
  }

  trait IDummyAliasTest {}

  @Alias("com.alias")
  case object DummAliasTest extends IDummyAliasTest

  case object Test
}
