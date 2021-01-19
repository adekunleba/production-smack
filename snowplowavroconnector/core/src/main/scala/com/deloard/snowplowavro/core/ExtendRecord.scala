package com.deloard.snowplowavro.core

import java.util.concurrent.atomic.AtomicInteger

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.settings.ClientConnectionSettings
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.http.scaladsl.model.{HttpRequest, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.ws.{Message, WebSocketRequest, WebSocketUpgradeResponse}
import akka.http.scaladsl.server.Directives.handleWebSocketMessages
import akka.stream.RestartSettings
import akka.stream.scaladsl.{Flow, RestartSource, Sink, Source}
import akka.util.ByteString
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * This trait is meant to help process some delegates data types for the
 * overall project, All incoming records must extend this trait.
 */
trait ExtendRecord extends org.apache.avro.specific.SpecificRecordBase {}

/**
 * For any new record coming for a particular webhook, we should subclass this class
 * And translate specifics of the webhook. This however should be able to manage all
 * levels of data from a webhook
 */
trait RecordMapper {

//  object ConnectType {
//    val
//  }

  private val recoredMapperLogger = LoggerFactory.getLogger(this.getClass)

  /**
   * In case we want to marshal from HttpRequest to Source
   */
  type RequestBuilder = HttpRequest => Future[HttpRequest]

  /**
   * Bind to an http
   * @param rs - routes
   * @param interface - Server running interface/webaddress
   * @param port - Server running port
   * @param connectionContext
   * @param actorSystem - Implicitly provided actor system
   * @param ec - Implicitly provided Execution context
   * @return
   */
  def bind(
    rs: Route,
    interface: String,
    port: Int,
    connectionContext: ConnectionContext = ConnectionContext.noEncryption()
  )(
    implicit actorSystem: ActorSystem,
    ec: ExecutionContext
  ): Future[Unit] =
    Http()
      .newServerAt(interface, port)
      .bind(rs)
      .map { binding =>
        recoredMapperLogger.info(s"REST interface bound to ${binding.localAddress}")
      }
      .recover { case ex =>
        recoredMapperLogger.error(
          "REST interface could not be bound to " +
            s"$interface:$port",
          ex.getMessage
        )
      }

  /**
   * Connect to a webhook api
   */
  def connect: Route

  /**
   * Using a remote service that streams user data over Websocket, this should be modelled as
   * a Source[T, NotUsed]. NotUsed shows the other type is of no importance (unit)
   * @return
   */
  def loadData: Future[Source[_, NotUsed]]

  /**
   * This is just a wrapper method to ensure that you are only working with source
   * and not a future.
   * @return
   */
  def loadDataSource: Source[Source[_, NotUsed], NotUsed] = Source.future(loadData)

//  def loadDataSource(path: String)(request: RequestBuilder): Source[HttpRequest, NotUsed] =
//    Source.future(request(HttpRequest(uri = path)))

  /**
   * Restart data source in case of failure of connection
   * This is important if the connection source fails, this will ensure
   * we continually pull data from the source.
   * @param settings
   * @param dataSource
   * @return
   */
  def restartDataSource(
    settings: RestartSettings,
    dataSource: Source[_, NotUsed]
  ): Source[_, NotUsed] =
    RestartSource.withBackoff(settings) { () =>
      dataSource
    }

  /**
   * Close the connection to the webhook API.
   */
  def closeConnection: Unit

  /**
   * ID Instance of the record being managed
   * @return
   */
  def id: Int

}

/**
 * These includes method that help us manipulate the various data contained in the webhook
 *
 * It should contain method to be able to manage all possible scenerios with the incoming webhook
 * payload
 */
trait MapRecordFunctions {

  /**
   * Parse a websocket message
   * @param parseAndApply uses Directive handleWebSocketMessage to parse a flow of
   *                      Message. Message is an ADT specific to Websocket in Akka.
   *                      A typical parseAndApply will be a processing from Flow[Any, Any, Any] =>
   *                      Flow[Message,Message,_]
   * @return
   */
  def handleRecord(parseAndApply: Flow[Message, Message, _]): Route =
    handleWebSocketMessages(parseAndApply)

  def closed: Future[Done]
}

trait SimpleWebSocketRecordMapper extends MapRecordFunctions with RecordMapper {

  private val simpleWsLogger = LoggerFactory.getLogger(this.getClass)

  /**
   * Try to create a server
   * @return
   */
//  override def connect: Route = {}
  /**
   * Initial implementation to keep alive by directing some click messages from the source.
   * @return
   */
  //implicit def keepAlive(source: Source[_, _]) = source.keepAlive()

  /**
   * Define websocket connection for the client to retrieve data from the websocket.
   * @param actorSystem
   * @return
   */
  def bindWebSocket(
    address: String,
    flow: Flow[Message, Message, Future[Done]] //TODO: SHould be able to parse the method's flow directly
  )(
    implicit actorSystem: ActorSystem,
    ev: ExecutionContext
  ): Future[Unit] = {

    /**
     * We implemented Keep alive for the websocket client by changing the default ClientConnectionSetting.
     */
    val clientSetting = ClientConnectionSettings(actorSystem)
    val pingCounter   = new AtomicInteger()
    val customWebsocketSettings =
      clientSetting.websocketSettings
        .withPeriodicKeepAliveData(() => ByteString(s"debug-${pingCounter.incrementAndGet()}"))

    val customSettings =
      clientSetting.withWebsocketSettings(customWebsocketSettings)
    val (upgradedResponse, _) =
      Http().singleWebSocketRequest(
        WebSocketRequest(address),
        flow,
        Http().defaultClientHttpsContext,
        None,
        customSettings,
        actorSystem.log
      )
    upgradedResponse
      .map { webSocket =>
        if (webSocket.response.status == StatusCodes.SwitchingProtocols) {
          simpleWsLogger.info(s"Websocket successfully bound to address:$address")
        }
      }
      .recover { case ex =>
        simpleWsLogger.error(
          "Web socket could not be bound to address  " +
            s"$address",
          ex.getMessage
        )
      }
  }

  /**
   * Both the Source and Sink should be declared based on the knoweldge of the data incoming from websocket
   */
  def source: Source[Message, _]
  def sink: Sink[Message, _]

  /**
   * Ideally, our websocket looks like we need it as only a Source, however to properly map the socket
   * We may need to also add a Sink.
   *
   * Need to find out how to parse only Source to the websocket and keep it alive
   * @param ec
   * @return
   */
  def flow(
    sink: Sink[Message, _] = sink,
    source: Source[Message, _] = source
  )(
    implicit ec: ExecutionContext
  ): Flow[Message, Message, Future[Done]] =
    Flow.fromSinkAndSource(sink, source).watchTermination() { case (_, f) =>
      f.onComplete {
        case Success(_)         => simpleWsLogger.info("Websocket connection to successfully closed")
        case Failure(exception) => simpleWsLogger.error(s"Websocket connection to uri failed ${exception.getMessage}")
      }
      f
    }

  /**
   * This websocket is one with only a source ignoring the sink
   * This is useful when you are only making request to the websocket
   * @param ec
   * @return
   */
  def flowWithNoSink(implicit ec: ExecutionContext): Flow[Message, Message, Future[Done]] = flow(Sink.ignore, source)

  /**
   * This websocket is one with only a Sink ignoring the sink
   * This is the most practical use case for where you only connect to the websocket to receive messages
   * from it to be dumped somewhere else.
   *
   * @param ec
   * @return
   */
  def flowWithNoSource(implicit ec: ExecutionContext): Flow[Message, Message, Future[Done]] = flow(sink, Source.maybe)

}

object RecordMapper {

  private object TAGS {
    val INNERSCHEMA = 1
  }
}
