package com.akkaexamples.babsde.akkastream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future
import scala.io.StdIn

object HttpStreamingExample extends App {

  implicit val system       = ActorSystem("http-streaming")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  val httpHello = Flow[HttpRequest].map({ request =>
    //This flow can do a lot of things actuallly, I think you should be able to send in messages to actores here
    // You can even extract the requests remember you are mapping a [[HttpRequest]] => [[HttpResponse}
    system.log.info("Responding through the flow with Hello there!")
    HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, ByteString("Hello there !")))
  })

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http().newServerAt(interface = "localhost", port = 8888).connectionSource()

  val streamServer: Future[Http.ServerBinding] = serverSource
    .to({
      Sink.foreach({ connection =>
        system.log.info("Accepted new connection from " + connection.remoteAddress)
        val response: NotUsed = connection.handleWith(httpHello)
        response
      })
    })
    .run() //Run until user preses enter

  StdIn.readLine() // User should press enter to end
  streamServer.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
