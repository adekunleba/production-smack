package com.fastdataakkaserver

import java.util.Date

import akka.actor.ActorRef
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{AsyncCallback, GraphStage, GraphStageLogic, StageLogging}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import twitter4j._
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

sealed trait ConfigurationException
case object WrongResourceTypeException extends ConfigurationException
case object WrongAuthenticationCredentials extends ConfigurationException

case class TweetEntityWithConf(tweetInstance: Twitter, credentials: TwitterCredentials)


sealed trait TweetProperties
case class Author(id:BigInt, name: String, screenName:String, description:Option[String],
                 location:Option[String], isProtected: Boolean, followerCounts:BigInt,
                  friendsCount:BigInt, isVerified:Boolean, statusCount:BigInt, authorCreatedDate:Date,
                  profileBackgroundColor:Option[String], profileTextColor: Option[String]) extends TweetProperties

case class Tweet(id: BigInt, text: String, entities:Entities, replyToStatusId:Option[BigInt],
                 replyToUserId:Option[BigInt], replyToScreenName: Option[String], tweetNumbers: TweetNumbers,
                ) extends TweetProperties
case class TweetNumbers(retweetCount: BigInt, favouriteCount:BigInt) extends TweetProperties
case class Entities(hashTags: Option[List[String]], symbols: Option[List[String]], userMentions: Option[List[String]]) extends TweetProperties



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
final case class IncomingMessage(author: Author, tweet: Tweet)

/**
 * Load an Akka Server that extracts tweets and dump into Kafka Server it should extend SmackOperation
 *
 * Some of the things our [[AkkaTwitterStreamer]] will do will be read tweet from
 */
object AkkaTwitterStreamer  extends LazyLogging {

  def login(resourceFilePath: String): Either[ConfigurationException, TweetEntityWithConf] = {
    val credentials = TwitterCredentials(path = resourceFilePath)

    if (credentials.validateConfig) {
      val builder = new ConfigurationBuilder()
      builder.setOAuthConsumerKey(credentials.consumerKey)
      builder.setOAuthConsumerSecret(credentials.consumerSecret)
      val factory = new TwitterFactory(builder.build())
      val t = factory.getInstance
      t.setOAuthAccessToken(new AccessToken(credentials.accessToken, credentials.accessTokenSecret))
      Right(TweetEntityWithConf(t, credentials))
    } else Left(WrongResourceTypeException)
  }

  /**
   * Let it manage either loginStream or Exception
   * If bad configuration is supplied in the resource file, should alert the user that a bad configuration is supplie
   * This is a fatal error which will not allow your streamer to work, let them supply a good configuration
   * Safely exit the application.
   * Else proceed with the application.
   */

  def loginStream(resourceFilePath: String): Either[ConfigurationException, TwitterStream] = {

    val twitterLogin = login(resourceFilePath)
    twitterLogin match {
      case Right(t) =>
        Try(t.tweetInstance.verifyCredentials()) match {
          case Success(_) =>
            lazy val conf = t.tweetInstance.getConfiguration
            val builder = new ConfigurationBuilder()
            builder.setOAuthConsumerKey(conf.getOAuthConsumerKey)
            builder.setOAuthConsumerSecret(conf.getOAuthConsumerSecret)
            builder.setOAuthAccessToken(t.credentials.accessToken)
            builder.setOAuthAccessTokenSecret(t.credentials.accessTokenSecret)
            logger.info(s"The Credentials for the twitter client ${conf.getOAuthConsumerKey}, " +
              s"${conf.getOAuthConsumerSecret}, ${t.credentials.accessToken}, ${t.credentials.accessTokenSecret}")
            val tsFactory = new TwitterStreamFactory(builder.build())
            val ts = tsFactory.getInstance
            Right(ts)
          case Failure(_) => Left(WrongAuthenticationCredentials)
        }
      case Left(x) => Left(x)
    }
  }

  /**
    * Listen to tweet from twitter. Given a client which is client of type TwitterStream
    * Because the configuration can return an error, to make use of the function, you need to load the configuration
    * and pass in the right value that shows successful connection to the client to listen
    *
    * TODO: Fix the listner
    * @param client
    * @param listener
    * @return
    */
  def listen(client: TwitterStream, actorRef:ActorRef,
             listener: StatusListener=this.listeners): Boolean = {

    client.addListener(listener(actorRef))
    client.sample()

    Thread.sleep(4000)

    client.cleanUp()
    client.shutdown()
    true
  }


  def listen(client: TwitterStream, listener: StatusListener): Boolean = {
    client.addListener(listener)
    client.sample()
    Thread.sleep(4000)

    client.cleanUp()
    client.shutdown()
    true
  }



  /**
    * Defining the listener for the twitter 4j library
    * @return
    */
  def listeners: StatusListener = new StatusListener() {
    /**
     * On a new Status extract important data
     * @param status
     */
    override def onStatus(status: Status): Unit = {

      val author :Author = getAuthors(status)
      val tweet = getTweets(status)

      //TODO: Connect the listener
      //Send to actorRef
      IncomingMessage(author, tweet)
    }
    /**
     * On Status Deletion Notice
     * @param statusDeletionNotice
     */
    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

    override def onStallWarning(warning: StallWarning): Unit = {
      logger.info(warning.getMessage)
      val message = warning.getMessage
    }

    override def onException(ex: Exception): Unit = ex.printStackTrace()
  }

  //Overloaded Listener with ActorRef
  def listeners(actorRef: ActorRef): StatusListener = new StatusListener() {
    /**
      * On a new Status extract important data
      * @param status
      */
    override def onStatus(status: Status): Unit = {
      val author :Author = getAuthors(status)
      val tweet = getTweets(status)
      actorRef  ! IncomingMessage(author, tweet)
    }
    /**
      * On Status Deletion Notice
      * @param statusDeletionNotice
      */
    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

    override def onStallWarning(warning: StallWarning): Unit = {
      logger.info(warning.getMessage)
      val message = warning.getMessage
    }

    override def onException(ex: Exception): Unit = ex.printStackTrace()
  }



  private def getAuthors(status: Status): Author = {
    Author(
      BigInt(status.getUser.getId),
      status.getUser.getName,
      status.getUser.getScreenName,
      Some(status.getUser.getDescription),
      Some(status.getUser.getLocation),
      status.getUser.isProtected,
      BigInt(status.getUser.getFollowersCount),
      BigInt(status.getUser.getFriendsCount),
      status.getUser.isVerified,
      BigInt(status.getUser.getStatusesCount),
      status.getUser.getCreatedAt,
      Some(status.getUser.getProfileBackgroundColor),
      Some(status.getUser.getProfileTextColor)
    )
  }


  private def getTweets(status: Status): Tweet = {
    Tweet(
      BigInt(status.getId),
      status.getText,
      getEntities(status),
      extractInReplies[Long](status.getInReplyToStatusId).map(x => BigInt(x)),
      extractInReplies[Long](status.getInReplyToUserId).map(x => BigInt(x)),
      extractInReplies[String](status.getInReplyToScreenName),
      getTweetNumbers(status)
    )
  }

  private def getTweetNumbers(status: Status) = {
    TweetNumbers(BigInt(status.getRetweetCount),
      BigInt(status.getFavoriteCount)
    )
  }

  private def getEntities(status: Status): Entities = {
    Entities(
      extractEntity(status.getHashtagEntities.toList),
      extractEntity(status.getSymbolEntities.toList),
      extractEntity(status.getUserMentionEntities.toList)
    )
  }

  //if anything is there then check if it's not an empty array
  private def extractEntity(x: List[TweetEntity]): Option[List[String]] = {
    x match {
      case a if a.nonEmpty => Some(a.map(x => x.getText))
      case _ => None}
  }

  private def extractInReplies[T](status:T) :Option[T] = {
    status match {
      case x if x != null => Some(x)
      case _ => None
    }
  }
}