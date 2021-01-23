package com.akkaexamples.babsde.akkastream.sampleAkkaIngestion

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{complete, handleWebSocketMessages, path, pathEndOrSingleSlash, _}
import akka.stream.QueueOfferResult.Failure
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source, SourceQueue}
import play.api.libs.json.Json

import scala.util.Success

object WebSocketServer extends App with AppContext {

  val simplePublisherGraph: RunnableGraph[SourceQueue[Event]] =
    Source
      .queue[Event](1024, OverflowStrategy.backpressure)
      .to(Sink.foreach(x => println(x)))

  val sourceQueue: SourceQueue[Event] = simplePublisherGraph.run()

  val queueWriter: Sink[Event, NotUsed] = Flow[Event]
    .mapAsync(1) { elem =>
      sourceQueue
        .offer(elem)
        .andThen({
          case Success(value: Failure) => system.log.info("Failed to publish data error in stream")
          case Success(_)              => system.log.info("Message published")
          case _                       => system.log.info("Failure in the try to publish")
        })
    }
    .to(Sink.ignore)

  val parseMessage: Flow[Message, Event, NotUsed] = Flow[Message].collect({
    case TextMessage.Strict(t) =>
      val js = Json.parse(t)
      Json.fromJson[Event](js).get
  })

  // Web Socket Handler Flow.
  val wsHandlerFlow: Flow[Message, Message, NotUsed] =
    Flow.fromSinkAndSource(parseMessage.to(queueWriter), Source.maybe)

  val routes = pathEndOrSingleSlash {
    complete("WS server is alive\n")
  } ~ path("connect") {
    handleWebSocketMessages(wsHandlerFlow)
  }

  Http()
    .newServerAt("localhost", 8123)
    .bind(routes)
    .onComplete {
      case Success(value)          => println(value)
      case scala.util.Failure(err) => println(err)
    }

  system.log.info("Listining at localhost port 8123")
  awaitTermination()
}
