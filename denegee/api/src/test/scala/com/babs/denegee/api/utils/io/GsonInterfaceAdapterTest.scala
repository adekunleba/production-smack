package com.babs.denegee.api.utils.io

import com.babs.denegee.api.test.{BaseClass, TestClass}
import com.google.gson.Gson
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class GsonInterfaceAdapterTest extends Matchers with AnyFunSpecLike {

  describe("When using GsonInterfaceAdapter") {

    it("should create a json for a class") {
      val gson: Gson = GsonInterfaceAdapterV2(classOf[TestClass])
      val test = TestClass("ben", "junior")
      val ser = gson.toJson(test)
      ser shouldBe s"""{"name":"ben","otherName":"junior","object-type":"${classOf[
        TestClass].getName}"}"""
      val baseClass = gson.fromJson(ser, classOf[BaseClass])
      test shouldBe baseClass
    }

  }

}
