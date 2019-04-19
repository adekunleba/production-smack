package com.fastdataakkaserver

import org.scalatest.{ Matchers, WordSpec }

//TODO: Write the first Test for your akka stream

class AkkaTwitterStreamerTestSuit extends WordSpec with Matchers {

  "A new Akka Stream server" can {

    "login to server" should {
      "load configuration from resource file " in {
        //AkkaTwitterStreamer.login("twitter") shouldBe 'right
        AkkaTwitterStreamer.login("twitterFake") shouldBe 'left
        AkkaTwitterStreamer.login("someBadTweet") shouldBe 'left
      }
    }
  }

}