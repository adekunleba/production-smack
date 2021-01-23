package com.akkaexamples.babsde.akkastream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Materializer}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

object StreamBroadcast extends App {
  implicit val system       = ActorSystem("actor-stream-1")
  implicit val materializer = Materializer.createMaterializer(system)

  val graphCreate = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import akka.stream.scaladsl.GraphDSL.Implicits._
    val in  = Source(0 to 10)
    val out = Sink.foreach(println)

    val bcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    val f1, f2 = Flow[Int].map(_ + 10)
    val f6     = Flow[Int].map(_ * 10)
    val f7     = Flow[Int].map(_ * 100)

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f6 ~> out
    bcast ~> f7 ~> merge
    ClosedShape
  }
  val graph  = RunnableGraph.fromGraph(graphCreate)
  val stream = graph.run()
//  Thread.sleep(1000)
}
