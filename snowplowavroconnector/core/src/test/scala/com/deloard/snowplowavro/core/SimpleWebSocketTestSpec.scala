package com.deloard.snowplowavro.core

import akka.{Done, NotUsed}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success

class SimpleWebSocketTestSpec extends WordSpec with Matchers with ActorSystemSetup {

  /**
   * Test web socket Client
   */

  class SimpleTestWebSocket extends SimpleWebSocketRecordMapper {

    /**
     * Both the Source and Sink should be declared based on the knoweldge of the data incoming from websocket
     */
    override def source: Source[Message, _] = Source.single(TextMessage("hello world!"))

    override def sink: Sink[Message, Future[Done]] = Sink.foreach { case message: TextMessage.Strict =>
      println("client received: " + message.text)
    }

    /**
     * Connect to a webhook api
     */
    override def connect: Route = ???

    /**
     * Using a remote service that streams user data over Websocket, this should be modelled as
     * a Source[T, NotUsed]. NotUsed shows the other type is of no importance (unit)
     *
     * @return
     */
    override def loadData: Future[Source[_, NotUsed]] = ???

    /**
     * Close the connection to the webhook API.
     */
    override def closeConnection: Unit = ???

    /**
     * ID Instance of the record being managed
     *
     * @return
     */
    override def id: Int = ???

    override def closed: Future[Done] = Future(Done)
  }

  "Websocket handler" should {
    "connect to websocket " in {
      val socket   = new SimpleTestWebSocket()
      val address  = "ws://echo.websocket.org/" // This address binds perfectly
      val address2 = "ws://webhook.site/ae38c4d4-8d1e-4ad5-a0e8-2fa1f7c4f7e0"
      val bind     = socket.bindWebSocket(address, socket.flowWithNoSource)
      Await.result(bind, Timeout(10 seconds).duration)
      bind.isCompleted shouldBe true
    }
  }
}
