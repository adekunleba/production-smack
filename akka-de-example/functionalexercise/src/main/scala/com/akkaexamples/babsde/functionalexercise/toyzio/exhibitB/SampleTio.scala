package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitB

object SampleTio extends TIOApp {

  /**
    * With this you can now compose TIO
    * since it has map and flatmaps
    */
  val run =
    for {
      _    <- TIO.effect(println("What is your name"))
      name <- TIO.effect(Console.in.readLine())
      _    <- TIO.Fail(new Throwable("boom")).recover(e => TIO.effect(println(s"Error message ${e.getMessage}")))
      _    <- TIO.effect(println(s"Hello some $name"))
    } yield ()
}
