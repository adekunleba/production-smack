package com.akkaexamples.babsde.akkastream

import akka.actor.ActorSystem
import akka.stream.{KillSwitches, Materializer, ThrottleMode}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.util.control.NoStackTrace
import scala.concurrent.duration._
import akka.stream._

import scala.concurrent.Await

object AdvancedFlowException extends App {

  implicit val system       = ActorSystem("advanced")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

  val exception = new Exception("Exception from Kill Swith") with NoStackTrace

  val stream = Source(1 to 10)
    .throttle(1, 100.millis, 1, ThrottleMode.shaping)
    .map(_ * 2)
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.foreach(println))(Keep.both)

  val (switch, fut) = stream.run()
  switch.abort(exception)
  Await.result(fut, 30.seconds)
}
