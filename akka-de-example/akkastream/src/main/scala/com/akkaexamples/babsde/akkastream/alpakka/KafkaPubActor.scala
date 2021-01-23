package com.akkaexamples.babsde.akkastream.alpakka

import akka.Done
import akka.actor.{Actor, Status}
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import com.akkaexamples.babsde.akkastream.alpakka.KafkaPubActor.{DataToPublish, PubProducer}
import org.apache.kafka.clients.producer.{Producer, ProducerRecord}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object KafkaPubActor {

  case class DataToPublish[K, V](
      data: ProducerRecord[K, V],
      futureConnector: Future[Producer[K, V]],
      producerSettings: ProducerSettings[K, V]
  )

  private case class PubProducer[K, V](
      data: ProducerRecord[K, V],
      futureConnector: Producer[K, V],
      producerSettings: ProducerSettings[K, V]
  )

}

class KafkaPubActor[K, V] extends Actor {
  import context._ //Needed for execution context
  override def receive: Receive = {
    case x: DataToPublish[K, V] =>
      val check = x.futureConnector andThen {
        case Success(value) =>
          val producer = x.producerSettings.withProducer(value)
          Source.single(x.data).runWith(akka.kafka.scaladsl.Producer.plainSink(producer))
        case Failure(exception) => Status.Failure(exception)
        // Sometimes, just do a context become with a method def x(app:T, app2:M):Receive
      }

    // TODO the actor is not sent to this arm with sender() ! PubProducer(x, y,z)
    case x: PubProducer[K, V] =>
      println("Receive message to create and send to producer")
      val producer = x.producerSettings.withProducer(x.futureConnector)
      println("Created producer only need to run with producer plainsink")
      Source.single(x.data).runWith(akka.kafka.scaladsl.Producer.plainSink(producer))
      println("Message sent to kafka")
  }
}
