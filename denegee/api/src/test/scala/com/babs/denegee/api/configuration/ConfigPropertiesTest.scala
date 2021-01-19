package com.babs.denegee.api.configuration

import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class ConfigPropertiesTest extends Matchers with AnyFunSpecLike {

  describe("When using ConfigProperties ") {
    it("should create an empty config") {
      val configProperties = ConfigProperties.empty[String, String]
      configProperties shouldBe a[ConfigProperties[_, _]]

    }
    it("should set and get properties") {
      val configProperties = ConfigProperties[String, String]()
      configProperties.setProperty("John", "Sure")
      assert(configProperties.getProperty("John").isDefined)
      configProperties.getProperty("John").get shouldBe "Sure"
    }

    it("should peek properties") {
      val configProperties = ConfigProperties[String, String]()
      configProperties.setProperty("John", "Sure")
    }

    it("should create multiple properties") {
      val config1 = ConfigProperties[String, String]()
      config1.setProperty("John", "Sure")
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      config1.getProperty("John").get shouldBe "Sure"
      config2.getProperty("John").get shouldBe "Mak"
    }

    it("should put properties") {
      val config1 = ConfigProperties[String, String]()
      val config2 = ConfigProperties[String, String]()
      config2.setProperty("John", "Mak")
      config1.putAll(config2)
      assert(config1.getProperty("John").isDefined)
    }

    it("should be same instance of config properties on new property added") {
      val config1 = ConfigProperties[String, String]()
      val initialConfig = config1
      config1.setProperty("John", "Sure")
      assert(initialConfig.equals(config1))
      config1.setProperty("Markel", "Mak")
      assert(initialConfig.equals(config1))

    }

    it("should create configuration from Map") {

      val mapConfig = Map("John" -> "sure", "Mike" -> "lema")
      val config = ConfigProperties(mapConfig)
      assert(config.getProperty("John").isDefined)
      assert(config.getProperty("Mike").isDefined)
      config.getprops shouldBe mapConfig
    }

  }
}
