package com.fastdataakkaserver

import org.scalatest.{ Matchers, WordSpec }

//TODO: Write the first Test for your akka stream

class AkkaTwitterStreamerTestSuit extends WordSpec with Matchers {

  "A new Akka Stream server" can {

    "loginStream to server" should {
      "load configuration from resource file " in {
        //AkkaTwitterStreamer.loginStream("twitter") shouldBe 'right
        AkkaTwitterStreamer.loginStream("twitterFake") shouldBe 'left
        AkkaTwitterStreamer.loginStream("someBadTweet") shouldBe 'left
      }

      "set up a listener tweet streamer " in {
        val loginCredentials = AkkaTwitterStreamer.loginStream("twitter")
        val result: Boolean = loginCredentials match {
          case Right(v) => AkkaTwitterStreamer.listen(v, AkkaTwitterStreamer.listeners)
          case Left(_) => false
        }
        result shouldBe true
      }
    }

    "Twitter Stream with akka" should {
      "connect to a Source of Incoming Tweets and print" in {
        val loginCredentials = AkkaTwitterStreamer.loginStream("twitter")
        val result: Boolean = loginCredentials match {
          case Right(v) => TweetActorPublisher.call()
          case Left(_) => false
        }
      }
    }
  }

}