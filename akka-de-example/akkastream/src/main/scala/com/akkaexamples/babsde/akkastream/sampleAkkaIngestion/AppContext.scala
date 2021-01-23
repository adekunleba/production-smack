package com.akkaexamples.babsde.akkastream.sampleAkkaIngestion

import akka.actor.ActorSystem
import akka.stream.Materializer

trait AppContext {

  implicit val system: ActorSystem        = ActorSystem("web-socket")
  implicit val materializer: Materializer = Materializer.createMaterializer(system)
  implicit val ec                         = system.dispatcher

  def awaitTermination() = {
    System.console().readLine() //wait for enter
    system.log.info(s"shutting down because enter was pressed")
    system.terminate()
  }
}
