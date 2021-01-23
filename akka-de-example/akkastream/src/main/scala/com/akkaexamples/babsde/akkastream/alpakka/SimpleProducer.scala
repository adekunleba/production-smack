package com.akkaexamples.babsde.akkastream.alpakka
import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.{Materializer, ThrottleMode}
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.duration.FiniteDuration

object SimpleProducer extends App {
  implicit val system       = ActorSystem("kafka-producer")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  // Read configuration - It automatically picks this from the application.conf
  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
  val topic            = "sampleTopic"

//  //Using the kafka producer
//  val kafkaproducer        = producerSettings.createKafkaProducerAsync()
//  val settingsWithProducer = producerSettings.withProducer(kafkaproducer)
  Source(1 to 100)
    .throttle(1, FiniteDuration(1, SECONDS), 1, ThrottleMode.Shaping)
    .map(num => {
      //construct our message here
      val message = s"Akka Scala Producer Message # ${num}"
      println(s"Message sent to topic - $topic - $message")
      new ProducerRecord[Array[Byte], String](topic, message.getBytes, message.toString)
    })
    .runWith(Producer.plainSink(producerSettings))
    .onComplete(_ => {
      println("All messages sent!")
      system.terminate()
    })
}
