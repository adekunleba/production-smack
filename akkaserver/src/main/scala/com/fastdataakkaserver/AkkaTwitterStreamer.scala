package com.fastdataakkaserver

import com.typesafe.config.{ Config, ConfigFactory }
import twitter4j.{ Twitter, TwitterFactory, TwitterStream, TwitterStreamFactory }
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._
import scala.util.{ Failure, Success, Try }

sealed trait ConfigurationException
case object WrongResourceTypeException extends ConfigurationException
case object WrongAuthenticationCredentials extends ConfigurationException

case class TwitterCredentials(path: String) {
  //Should check all necessary credentials keys exists.

  def validateConfig: Boolean = {
    val entrySet = twitterConfig.entrySet()
    entrySet.asScala.exists(x => List("consumerKey", "consumerSecret", "accessToken", "accessTokenSecret")
      .contains(x.getKey))
  }

  lazy val configLoader: Config = ConfigFactory.load()
  lazy val twitterConfig: Config = configLoader.getConfig(path)

  lazy val consumerKey: String = twitterConfig.getString("consumerKey")
  lazy val consumerSecret: String = twitterConfig.getString("consumerSecret")
  lazy val accessToken: String = twitterConfig.getString("accessToken")
  lazy val accessTokenSecret: String = twitterConfig.getString("accessTokenSecret")
}

/**
 * Load an Akka Server that extracts tweets and dump into Kafka Server it should extend SmackOperation
 *
 * Some of the things our AkkaTwitterStreamer will do will be read tweet from
 */
object AkkaTwitterStreamer {

  /**
   * Let it manage either login or Exception
   * If bad configuration is supplied in the resource file, should alert the user that a bad configuration is supplie
   * This is a fatal error which will not allow your streamer to work, let them supply a good configuration
   * Safely exit the application.
   * Else proceed with the application.
   */

  def login(resourceFilePath: String): Either[ConfigurationException, TwitterStream] = {
    val credentials = TwitterCredentials(path = resourceFilePath)

    if (credentials.validateConfig) {
      val builder = new ConfigurationBuilder()
      builder.setOAuthConsumerKey(credentials.consumerKey)
      builder.setOAuthConsumerSecret(credentials.consumerSecret)
      val factory = new TwitterFactory(builder.build())
      val t = factory.getInstance
      t.setOAuthAccessToken(new AccessToken(credentials.accessToken, credentials.accessTokenSecret))
      val tsFactory = new TwitterStreamFactory(t.getConfiguration)
      val ts = tsFactory.getInstance
      //Test Credential connection
      Try(t.verifyCredentials()) match {
        case Success(_) => Right(ts)
        case Failure(_) => Left(WrongAuthenticationCredentials)
      }
    } else Left(WrongResourceTypeException)
  }

  //  def testLoginCredentials(tweetStream: TwitterStream): Try[String] = {
  //    Try(tweetStream.sample("EN"))
  //  }

  //Why Use protected here

}