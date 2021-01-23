package com.akkaexamples.babsde.akkastream.alpakka

import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{ActorSystem, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{Materializer, ThrottleMode}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
//import akka.pattern.pipe

import scala.concurrent.duration.FiniteDuration

object SimpleProducerReUse extends App {
  implicit val system       = ActorSystem("kafka-producer")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  // Read configuration - It automatically picks this from the application.conf
  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
  val topic            = "sampleTopic2"

  // Using an async Kafka producer to fail gracefully I believe.
  val kafkaproducer        = producerSettings.createKafkaProducer()
  val kafkaProducerAsync   = producerSettings.createKafkaProducerAsync()
  val settingsWithProducer = producerSettings.withProducer(kafkaproducer)
//  import akka.pattern.pipe
//  kafkaProducerAsync.pipeTo(system.actorOf(KafkaPubActor[Array[Byte], String]))

  val producerActor = system.actorOf(Props[KafkaPubActor[Array[Byte], String]], "kafka-pub")

  Source(1 to 100)
    .throttle(1, FiniteDuration(1, SECONDS), 1, ThrottleMode.Shaping)
    .map(num => {
      //construct our message here
      val message = s"Akka Scala Producer Message # ${num}"
      println(s"Message sent to topic - $topic - $message")
      new ProducerRecord[Array[Byte], String](topic, message.getBytes, message.toString)
    }) // Source[Producer[K, v], NotUsed]
    // Use actor to send data to kafka.
    .map { data =>
      producerActor ! KafkaPubActor.DataToPublish(data, kafkaProducerAsync, producerSettings)
    }
    .runWith(Sink.ignore)
    .onComplete(_ => {
      println("All messages sent!")
      kafkaProducerAsync.foreach(p => p.close())
      kafkaproducer.close()
      system.terminate()
    })
}
