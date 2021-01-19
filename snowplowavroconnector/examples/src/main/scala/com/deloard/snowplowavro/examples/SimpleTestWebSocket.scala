package com.deloard.snowplowavro.examples

import akka.{Done, NotUsed}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.deloard.snowplowavro.core.SimpleWebSocketRecordMapper

import scala.concurrent.{ExecutionContext, Future}

class SimpleTestWebSocket(implicit val executionContext: ExecutionContext) extends SimpleWebSocketRecordMapper {

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
