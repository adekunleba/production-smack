package com.babs.denegee.api.utils

import com.babs.denegee.reflect.Alias
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class ClassAliasResolverSpec extends Matchers with AnyFunSpecLike {

  describe("When using ClassAliasResolver ") {

    it("Should correctly get list to scan from config") {
      val resolver: ClassAliasResolver[IDummyAliasTest] =
        ClassAliasResolver(classOf[IDummyAliasTest])
      resolver.packagesToScan shouldBe List("com.babs.denegee", "denegee", "com.linkedin.gobblin")
    }
    it("should validate that cache map created for annotate subclass") {
      val resolver: ClassAliasResolver[IDummyAliasTest] =
        ClassAliasResolver(classOf[IDummyAliasTest])
      resolver.packageMap.isEmpty shouldBe false
    }
    it("Should resolve the class name if its aliased return name if not alias present") {
      val resolver: ClassAliasResolver[IDummyAliasTest] =
        ClassAliasResolver(classOf[IDummyAliasTest])
      resolver.resolve("com.alias") shouldBe DummAliasTest.getClass.getName
      resolver.resolve("abc") shouldBe "abc"
    }

  }

  trait IDummyAliasTest {}

  @Alias("com.alias")
  case object DummAliasTest extends IDummyAliasTest
}
