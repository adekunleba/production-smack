package com.babs.denegee
package config

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

final class CommonSettingsSpec extends AnyWordSpecLike with Matchers {

  "name" should {
    "be equal to denegee" in {
      CommonSettings.name shouldEqual "denegee_test"
    }
  }

  "log file name" should {
    "be equal to name" in {
      CommonSettings.logName shouldEqual CommonSettings.name
    }
  }

  "application config" should {
    "load application configuration" in {
      CommonSettings.applicationConfig.getString("sample") shouldBe "config"
    }
  }

  import CommonSettingsImplicits._
  import cats.syntax.all._
  "application config" should {
    "load config list if present" in {
      CommonSettings.applicationConfig.getOptionalStrList("sample-list") shouldBe List(
        "open",
        "close"
      ).some
      CommonSettings.applicationConfig.getOptionalStrList("scan-packages") shouldBe None
    }
  }

}
