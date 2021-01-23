package com.akkaexamples.babsde.akkastream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source

object MostBasicExample extends App {

  implicit val system      = ActorSystem("StreamExample")
  implicit val materilizer = Materializer.createMaterializer(system)
  implicit val ec          = system.dispatcher

  val source: Source[Int, NotUsed]        = Source(1 to 10)
  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)
  val done                                = factorials.runForeach(i => println(i))

  done.onComplete(_ => system.terminate())
}
