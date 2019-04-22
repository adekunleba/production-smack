package com.fastdataakkaserver

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import org.reactivestreams.Publisher
import twitter4j.TwitterStream



/***
  * Using Actor Publisher, one can invoke sending tweets as a actor ref message
  * and chain it as a source.
  */
object TweetActorPublisher extends LazyLogging{

  implicit val system = ActorSystem("Twitter-Status-Listener")
  implicit val materializer = ActorMaterializer()



  val (actorRef, incomingMessage) = Source.actorRef[IncomingMessage](1000, OverflowStrategy.backpressure)
    .toMat(Sink.asPublisher(true))(Keep.both).run() //Until you run, you won't get a runnable graph


  def call(actorRef: ActorRef, messagePublisher: Publisher[IncomingMessage],
           client:TwitterStream): Source[IncomingMessage, NotUsed] = {

    AkkaTwitterStreamer.listen(client, actorRef)
    //Looks like Source.from --  Multiple Source will be usefull in our Graph Stage
    logger.info("Listener created and sending message to Publisher")
    Source.fromPublisher(messagePublisher)
  }


}