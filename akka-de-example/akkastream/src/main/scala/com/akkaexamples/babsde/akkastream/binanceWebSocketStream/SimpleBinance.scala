package com.akkaexamples.babsde.akkastream.binanceWebSocketStream

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.{Future, Promise}

object SimpleBinance extends App {

  implicit val system       = ActorSystem("binance-simple")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  val sink: Sink[Message, Future[Done]] = Sink.foreach[Message]({ record =>
    system.log.info(s"Message recieved ${record.toString}")
  })

  val flow: Flow[Message, Message, Promise[Option[Nothing]]] = Flow.fromSinkAndSourceMat(sink, Source.maybe)(Keep.right)

  val (upgradeResponse, promise) =
    Http().singleWebSocketRequest(WebSocketRequest("wss://stream.binance.com:9443/ws/btcusdt@aggTrade"), flow)

  val connected = upgradeResponse.map { upgrade =>
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      println("switching protocols")
      Done
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }
  connected.onComplete(println)
}
