package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitE

import java.time.LocalDateTime

import scala.concurrent.duration._
object SampleTio extends TIOApp {

  /**
    * With this you can now compose TIO
    * since it has map and flatmaps
    */
  override def run =
    for {
      _ <- TIO.effect(println(s"${LocalDateTime.now()} start"))
      _ <- simpleSleep(1.seconds) // This basically runs on a seperate thread
      _ <- sampleForEach(1 to 10000)(i => TIO.effect(println(s"count value $i")))
      _ <- TIO.effect(println(s"${LocalDateTime.now()} end"))
    } yield ()

  // This will yield a stackoverflow if ran for a large value because
  // TIO is not asynchronous yet
  // DONOT USE
  def sampleForEach[A, B](xs: Iterable[A])(f: A => TIO[B]): TIO[Iterable[B]] =
    xs.foldLeft(TIO.effect(Vector.empty[B]))((acc, curr) =>
      for {
        soFar <- acc     // Because it is TIO (flatmap is present)
        x     <- f(curr) // f: function maps A => TIO{B]
      } yield soFar :+ x
    )
}
