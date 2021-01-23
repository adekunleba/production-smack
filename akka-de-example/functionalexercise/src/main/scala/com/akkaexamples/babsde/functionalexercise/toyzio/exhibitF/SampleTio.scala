package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitF

import java.time.LocalDateTime

import scala.concurrent.duration._
object SampleTio extends TIOApp {

  /**
    * With this you can now compose TIO
    * since it has map and flatmaps
    */
  override def run =
    for {
      _ <- TIO.effect(println(s"${LocalDateTime.now()} main fibre before fork"))
      fibre <- {
        TIO.effect(println(s"${LocalDateTime.now()} forked fibre before sleep")) *>
          simpleSleep(4.seconds) *>
          TIO.effect(println(s"${LocalDateTime.now()} forked fibre after sleep")) *>
          TIO.effect(1)
      }.fork
      _   <- TIO.effect(println(s"${LocalDateTime.now()} main fibre before another sleep"))
      _   <- simpleSleep(1.seconds) // This basically runs on a seperate thread
      _   <- TIO.effect(println(s"${LocalDateTime.now()} main fibre after fork"))
      res <- fibre.join
      _   <- TIO.effect(println(s"${LocalDateTime.now()} forked fibre result: $res"))
    } yield ()

  // This will yield a stackoverflow if ran for a large value because
  // TIO is not asynchronous yet
  // DONOT USE

}
