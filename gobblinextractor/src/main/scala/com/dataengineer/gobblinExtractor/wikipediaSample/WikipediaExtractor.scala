package com.dataengineer.gobblinExtractor.wikipediaSample

import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean

import com.dataengineer.gobblinExtractor.wikipediaSample.GobblinExtractorImplicits._
import com.google.gson.{Gson, JsonElement, JsonObject}
import org.apache.gobblin.configuration.{ConfigurationKeys, WorkUnitState}
import org.apache.gobblin.http.{
  HttpClientConfigurator,
  HttpClientConfiguratorLoader
}
import org.apache.gobblin.records.RecordStreamWithMetadata
import org.apache.gobblin.source.extractor.Extractor
import org.apache.gobblin.stream.{RecordEnvelope, StreamEntity}
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.client.utils.{URIBuilder, URLEncodedUtils}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.{HttpResponse, HttpStatus}
import resource._

import scala.collection.JavaConverters._
import scala.util.Try

final case class JsonResult(memberTitle: String, idObject: JsonElement)

class WikipediaExtractor(workUnitState: WorkUnitState)
    extends Extractor[String, JsonElement] {

  private val WIKIPEDIA_AVRO_SCHEMA = "wikipedia.avro.schema"
  private val CONFIG_PREFIX = "gobblin.wikipediaSource."
  private val HTTP_CLIENT_CONFIG_PREFIX = CONFIG_PREFIX + "httpClient."
  private val WIKIPEDIA_API_ROOTURL = "wikipedia.api.rooturl"
  private val JSON_MEMBER_QUERY = "query"
  private val JSON_MEMBER_PAGES = "pages"
  private val JSON_MEMBER_REVISIONS = "revisions"
  private val JSON_MEMBER_PAGEID = "pageid"
  private val JSON_MEMBER_TITLE = "title"

  // Create http client configurator from
  private val httpClientConfigurator: HttpClientConfigurator =
    new HttpClientConfiguratorLoader(workUnitState).getConfigurator
      .setStatePropertiesPrefix(HTTP_CLIENT_CONFIG_PREFIX)
      .configure(workUnitState)

  private val httpClient = Option(httpClientConfigurator.createClient())

  override def getSchema: String = {
    readProp(WIKIPEDIA_AVRO_SCHEMA, workUnitState)
  }

  override def getExpectedRecordCount: Long = return 0

  override def getHighWatermark: Long = ???

  override def close(): Unit = ???

  override def readRecord(reuse: JsonElement): JsonElement =
    super.readRecord(reuse)

  override def readStreamEntity(): StreamEntity[JsonElement] =
    super.readStreamEntity()

  override def readRecordEnvelope(): RecordEnvelope[JsonElement] =
    super.readRecordEnvelope()

  override def recordStream(
    shutdownRequest: AtomicBoolean
  ): RecordStreamWithMetadata[JsonElement, String] =
    super.recordStream(shutdownRequest)

  private def readProp(key: String, state: WorkUnitState) = {
    val value: String = Seq(
      Option(workUnitState.getWorkunit.getProp(key)),
      Option(workUnitState.getProp(key))
    ).find(_.isDefined)
      .flatten
      .getOrElse(workUnitState.getJobState.getProp(key))
    value
  }

  def createHttpRequest(
    rootUrl: String,
    query: Map[String, String]
  ): Either[Throwable, HttpGet] = {
    val queryTokens: Seq[BasicNameValuePair] = query.toSeq.map { pair =>
      new BasicNameValuePair(pair._1, pair._2)
    }

    val encodedQuery: Either[Throwable, String] =
      throwableLeft {
        URLEncodedUtils.format(queryTokens.asJava, StandardCharsets.UTF_8)
      }

    encodedQuery.flatMap(
      enString =>
        throwableLeft {
          new HttpGet(new URIBuilder(rootUrl).setQuery(enString).build())
      }
    )
  }

  def retrievePageRevisions(
    query: Map[String, String]
  ): Option[List[JsonObject]] = {
    val gson = new Gson()
    val rootUrl = readProp(WIKIPEDIA_API_ROOTURL, workUnitState)
    val request = createHttpRequest(rootUrl, query)

    // This is just for us to complete the code, naturally you should propagate the error till the end of the code.
    val getRequest = request.toOption.get

    /**
      * TODO: Work on refactoring the session management for this aspect
      * Use the answer here https://stackoverflow.com/a/2219494 to make a closabe for opton[T] of any clossable.
      */
    val check = managed(
      sendHttpRequest(getRequest, httpClient).get
        .asInstanceOf[CloseableHttpResponse]
    ).flatMap(
      x =>
        managed(
          new BufferedReader(
            new InputStreamReader(
              x.getEntity.getContent,
              ConfigurationKeys.DEFAULT_CHARSET_ENCODING
            )
          )
      )
    )

    // FInd gets you the actual element e.g
    val another = check.map(
      x => Stream.continually(x.readLine()).takeWhile(_ != null).mkString(" ")
    )

    // Filter out the JsonElement with possible errors
    val jsonElement = another.opt
      .map(result => gson.fromJson(result, classOf[JsonElement]))
      .filter({ x =>
        val isJsonObject = x.isJsonNull
        val isJsonObjectQuery = Try(
          x.getAsJsonObject
            .getAsJsonObject(JSON_MEMBER_QUERY)
            .getAsJsonObject(JSON_MEMBER_PAGES)
        ).toOption

        val isPageObjectEmpty = isJsonObjectQuery.filter(!_.entrySet().isEmpty)
        isJsonObject && isJsonObjectQuery.isDefined && isPageObjectEmpty.isDefined
      })
      .map({ elem =>
        val pageIdObj = elem.getAsJsonObject
          .getAsJsonObject(JSON_MEMBER_QUERY)
          .getAsJsonObject(JSON_MEMBER_PAGES)
        pageIdObj.getAsJsonObject(pageIdObj.entrySet().iterator().next().getKey)
      })

    // Transform the elements to actual needed Json Object
    jsonElement
      .map({ pageIdObj =>
        if (pageIdObj.has(JSON_MEMBER_REVISIONS)) {
          val jsonArr = pageIdObj.getAsJsonArray(JSON_MEMBER_REVISIONS)
          jsonArr.iterator().asScala.foldRight(List.empty[JsonObject]) {
            case (jsonElem, ls) =>
              val revObj = jsonElem.getAsJsonObject
              if (pageIdObj.has(JSON_MEMBER_PAGEID))
                revObj
                  .add(JSON_MEMBER_PAGEID, pageIdObj.get(JSON_MEMBER_PAGEID))
              if (pageIdObj.has(JSON_MEMBER_TITLE))
                revObj.add(JSON_MEMBER_TITLE, pageIdObj.get(JSON_MEMBER_TITLE))
              revObj :: ls
          }
        } else List.empty[JsonObject]
      })
  }

  def sendHttpRequest(request: HttpGet,
                      client: Option[HttpClient]): Option[HttpResponse] = {
    // Use httpClient to make a request
    val response = client.map(x => x.execute(request))
    response.filterNot(
      x =>
        x.getStatusLine.getStatusCode != HttpStatus.SC_OK || Option(x.getEntity).isDefined
    )
  }
}

object WikipediaExtractor {

  def apply(workUnitState: WorkUnitState): WikipediaExtractor =
    new WikipediaExtractor(workUnitState)
}
