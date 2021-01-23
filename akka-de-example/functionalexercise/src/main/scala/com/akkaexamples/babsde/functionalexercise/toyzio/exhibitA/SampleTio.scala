package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitA

object SampleTio extends App {

  /**
    * With this you can now compose TIO
    * since it has map and flatmaps
    */
  Runtime.run({
    for {
      _    <- TIO.effect(println("What is your name"))
      name <- TIO.effect(Console.in.readLine())
      _    <- TIO.effect(println(s"Hello some $name"))
    } yield ()
  })
}
