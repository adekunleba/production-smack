package com.deloard.snowplowavro.core

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, Suite}
import scala.concurrent.duration._
import scala.concurrent.Await

trait ActorSystemSetup extends BeforeAndAfterAll { self: Suite =>
  implicit val system       = ActorSystem("test-system")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  override def afterAll: Unit = {
    materializer.shutdown()
    Await.result(system.terminate(), Timeout(10 seconds).duration)
  }
}
