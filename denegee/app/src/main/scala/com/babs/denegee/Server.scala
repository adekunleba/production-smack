package com.babs.denegee

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.babs.denegee.common.config.AppSettings
import io.prometheus.client.hotspot.DefaultExports
import com.babs.denegee.http.Web

import scala.concurrent.ExecutionContext

object Server extends Web with App {

  // must be declared lazy to be used by Route traits
  implicit protected[this] lazy val actorSystem: ActorSystem = ActorSystem(
    "denegee")
  implicit protected[this] val materializer: ActorMaterializer =
    ActorMaterializer()
  implicit protected[this] val executionContext: ExecutionContext =
    actorSystem.dispatcher

  private[this] lazy val httpConfig = AppSettings(actorSystem).Http
  implicit protected[this] val timeout: Timeout = httpConfig.timeout

  private[this] val log = Logging(actorSystem, getClass.getName)

  // JVM metrics from MBeans
  DefaultExports.initialize()

  bindAndHandleHttp {
    log.debug("run server")
  }

}
