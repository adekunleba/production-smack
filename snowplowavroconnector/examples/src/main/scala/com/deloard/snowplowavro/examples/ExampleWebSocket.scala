package com.deloard.snowplowavro.examples

import akka.actor.ActorSystem
import akka.stream.Materializer

/**
 * Example Project to Run
 */
object ExampleWebSocket extends App {

  implicit val actorSystem        = ActorSystem(name = "example-websocket-extractor")
  implicit val streamMaterializer = Materializer.createMaterializer(actorSystem)
  implicit val executionContext   = actorSystem.dispatcher
  val log                         = actorSystem.log

  /** Create the socket connection* */
  val socket = new SimpleTestWebSocket()

  val address = "wss://echo.websocket.org/"
  val bind    = socket.bindWebSocket(address, socket.flowWithNoSource)

}
