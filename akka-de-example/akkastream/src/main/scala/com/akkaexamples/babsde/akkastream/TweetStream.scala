package com.akkaexamples.babsde.akkastream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Materializer}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

object TweetStream extends App {

  final case class Author(handle: String)
  final case class HashTag(name: String)
  final case class Tweet(author: Author, timestamp: Long, body: String) {
    // Tweet object can be easily converted to hashtag
    def hashtags: Set[HashTag] =
      body
        .split(" ")
        .collect({
          case t if t.startsWith("#") => HashTag(t.replaceAll("[^#\\w]", ""))
        })
        .toSet
  }

  val akkaTag = HashTag("akka")

  // Source from a list of tweet objects.
  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
      Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
      Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
      Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
      Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
      Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
      Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
      Nil
  )

  implicit val system       = ActorSystem("Tweet-reactive")
  implicit val materializer = Materializer.createMaterializer(system)
  implicit val ec           = system.dispatcher

//  val done = tweets.map(_.hashtags).reduce(_ ++ _).mapConcat(identity).runWith(Sink.foreach(x => println(x)))

  // Alternatively I can create a sink with flow
  def printSink   = Flow[String].toMat(Sink.foreach(x => println(x)))(Keep.right)
  val tweetSource = tweets.map(_.hashtags).reduce(_ ++ _).mapConcat(identity)
  val done        = tweetSource.map(_.name.toUpperCase).runWith(printSink)

  done.onComplete(_ => system.terminate())

}
