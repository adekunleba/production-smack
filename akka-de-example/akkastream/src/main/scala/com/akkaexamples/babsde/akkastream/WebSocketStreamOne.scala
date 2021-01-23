package com.akkaexamples.babsde.akkastream

import akka.NotUsed
import akka.pattern.Patterns.after
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer, javadsl}
import akka.stream.scaladsl.{Flow, Source}

import scala.collection.immutable.Seq
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Accepts strings over websocket at ws://127.0.0.1/measurements
  * Protects the "database" by batching element in groups of 1000 but makes sure to at least
  * write every 1 second to not write too stale data or loose too much on failure.
  *
  * Based on this (great) blog article by Colin Breck:
  * http://blog.colinbreck.com/akka-streams-a-motivating-example/
  */
object WebSocketStreamOne extends App {

  object Database {
    def asyncBulkInsert(entries: Seq[String])(implicit system: ActorSystem): Future[Seq[String]] =
      // simulate that writing to a database takes ~30 millis
      after(30.millis, system.scheduler, system.dispatcher, Future.successful(entries))

  }

  implicit val system = ActorSystem()
  implicit val ec     = system.dispatcher
  implicit val mat    = Materializer.createMaterializer(system)

  val measurementsFlow =
    Flow[Message]
      .flatMapConcat { message =>
        // handles both strict and streamed ws messages by folding
        // the later into a single string (in memory)
        message.asTextMessage.getStreamedText.asScala.fold("")(_ + _)

      }
      .groupedWithin(1000, 1.second)
      .mapAsync(5)(Database.asyncBulkInsert _)
      .map(written => TextMessage("wrote up to: " + written.last))

  val route: Route =
    path("measurements") {
      get {
        handleWebSocketMessages(measurementsFlow)
      }
    }

  val futureBinding: Future[Http.ServerBinding] = Http().newServerAt("127.0.0.1", 8080).bind(route)

  futureBinding.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      println(s"Akka HTTP server running at ${address.getHostString}:${address.getPort}")

    case Failure(ex) =>
      println(s"Failed to bind HTTP server: ${ex.getMessage}")
      ex.fillInStackTrace()

  }

}
