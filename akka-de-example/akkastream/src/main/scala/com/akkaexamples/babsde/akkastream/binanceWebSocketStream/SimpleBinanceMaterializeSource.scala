package com.akkaexamples.babsde.akkastream.binanceWebSocketStream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.QueueOfferResult.Failure
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, SourceQueue, SourceQueueWithComplete}
import play.api.libs.json.Json

import scala.concurrent.Promise
import scala.util.Success

object SimpleBinanceMaterializeSource extends App {

  implicit val system       = ActorSystem("binance-simple")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  val simplePublisherGraph: RunnableGraph[SourceQueueWithComplete[BinanceAggTradeEvent]] =
    Source
      .queue[BinanceAggTradeEvent](1024, OverflowStrategy.backpressure)
      .to(Sink.foreach(x => println(x)))

  val sourceQueue: SourceQueueWithComplete[BinanceAggTradeEvent] = simplePublisherGraph.run()

  val queueWriter: Sink[BinanceAggTradeEvent, NotUsed] = Flow[BinanceAggTradeEvent]
    .mapAsync(1) { elem =>
      sourceQueue
        .offer(elem)
        .andThen({
          case Success(value: Failure) => system.log.info("Failed to publish data error in stream")
          case Success(value)          => system.log.info("Message published")
          case _                       => system.log.info("Failure in the try to publish")
        })
    }
    .to(Sink.ignore)

  val parseMessages: Flow[Message, BinanceAggTradeEvent, NotUsed] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(t) =>
          val js = Json.parse(t)
          Json.fromJson[BinanceAggTradeEvent](js).get
      }

  val flow: Flow[Message, Nothing, Promise[Option[Nothing]]] =
    Flow.fromSinkAndSourceMat(parseMessages.to(queueWriter), Source.maybe)(Keep.right)

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
