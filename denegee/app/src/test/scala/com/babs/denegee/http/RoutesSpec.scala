package com.babs.denegee
package http

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}
import com.babs.denegee.Server.routes
import com.babs.denegee.http.route.Status

// scalastyle:off underscore.import
import io.circe.generic.auto._
import io.circe.syntax._
// scalastyle:on underscore.import

final class RoutesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "routes /status" should {
    "return OK for GET requests to the root path" in {
      Get("/status") ~> routes ~> check {
        responseAs[String] shouldEqual Status("OK").asJson.noSpaces
      }
    }
  }

}
