import sbt._
import Versions._

object Dependencies {

  //Akka Dependencis
  val akkaHttp =                "com.typesafe.akka"     %% "akka-http"                % akkaHttpVersion
  val akkaHttpSprayJson =       "com.typesafe.akka"     %% "akka-http-spray-json"     % akkaHttpVersion
  val akkaHttpXml =             "com.typesafe.akka"     %% "akka-http-xml"            % akkaHttpVersion
  val akkaStream =              "com.typesafe.akka"     %% "akka-stream"              % akkaVersion

  //Twitter Dependencies
  val twitter4jCore =           "org.twitter4j"         % "twitter4j-core"            % twitter4jVersion
  val twitter4jStream =         "org.twitter4j"         % "twitter4j-stream"          % twitter4jVersion


  //Spark Dependencies



  //Cassandra Dependencies



  //Kafka Dependencies






  //Bringing together all dependencies
  lazy val akkaDependencies = Seq(
    akkaHttp,
    akkaHttpSprayJson,
    akkaHttpXml,
    akkaStream,
    twitter4jCore,
    twitter4jStream
  )

}