import sbt._

object Dependencies {

  object Version {
    // Listed in alphabetical order
    lazy val AwsSageMakerRuntime = "1.11.928"
    lazy val AkkaHttpVersion     = "10.2.1"
    lazy val AkkaVersion         = "2.6.10"
    lazy val AlpakkaKafka        = "2.0.6"
    lazy val AvroVersion         = "1.10.0"
    lazy val CirceVersion        = "0.12.3"
    lazy val ConfluentVersion    = "5.4.2"
    lazy val JacksonVersion      = "2.10.5.1"
    lazy val KafkaVersion        = "2.4.1"
    lazy val LogbackVersion      = "1.2.3"
    lazy val PlayJson            = "2.8.1"
    lazy val TyplevelJawn        = "1.0.0"

    val ScalaCheck            = "1.13.4"
    lazy val ScalaTestVersion = "3.1.0"
  }

  object Library {
    val awsSagemakerRuntime = "com.amazonaws"              % "aws-java-sdk-sagemakerruntime" % Version.AwsSageMakerRuntime
    val akkaHttp            = "com.typesafe.akka"          %% "akka-http"                    % Version.AkkaHttpVersion
    val akkaHttpJson        = "com.typesafe.akka"          %% "akka-http-spray-json"         % Version.AkkaHttpVersion
    val akkaActorTyped      = "com.typesafe.akka"          %% "akka-actor-typed"             % Version.AkkaVersion
    val akkaStream          = "com.typesafe.akka"          %% "akka-stream"                  % Version.AkkaVersion
    val alpakkaKafka        = "com.typesafe.akka"          %% "akka-stream-kafka"            % Version.AlpakkaKafka
    val playJson            = "com.typesafe.play"          %% "play-json"                    % Version.PlayJson
    val jackson             = "com.fasterxml.jackson.core" % "jackson-databind"              % Version.JacksonVersion
    val jawnParser          = "org.typelevel"              %% "jawn-parser"                  % Version.TyplevelJawn
    val jawnAst             = "org.typelevel"              %% "jawn-ast"                     % Version.TyplevelJawn
    val kafka               = "org.apache.kafka"           %% "kafka"                        % Version.KafkaVersion
    val kafkaClient         = "org.apache.kafka"           % "kafka-clients"                 % Version.KafkaVersion
    val confluentSerializer = "io.confluent"               % "kafka-avro-serializer"         % Version.ConfluentVersion
//    val circeJson      = "io.circe"                   %% "circe-core"               % Version.CirceVersion
//    val circeGeneric   = "io.circe"                   %% "circe-generic"            % Version.CirceVersion
    val logback          = ("ch.qos.logback" % "logback-classic" % Version.LogbackVersion).exclude("org.slf4j", "slf4j-api")
    val scalaLogging     = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    val akkaHttpTest     = "com.typesafe.akka" %% "akka-http-testkit" % Version.AkkaHttpVersion % Test
    val akkaTypeTest     = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.AkkaVersion % Test
    val akkaStreamTest   = "com.typesafe.akka" %% "akka-stream-testkit" % Version.AkkaVersion % Test
    val alpakkaKafkaTest = "com.typesafe.akka" %% "akka-stream-kafka-testkit" % Version.AlpakkaKafka % Test
    val scalaTest        = "org.scalatest" %% "scalatest" % Version.ScalaTestVersion % Test
    val scalaCheck       = "org.scalacheck" %% "scalacheck" % Version.ScalaCheck % Test
  }
  val coreLibrary = Seq(
    Library.awsSagemakerRuntime,
    Library.akkaHttp,
    Library.akkaActorTyped,
    Library.akkaHttpJson,
    Library.akkaStream,
    Library.playJson,
    Library.scalaTest,
    Library.logback,
    Library.scalaLogging,
    Library.scalaLogging
  )

  val akkaCoreLibrary = Seq(
    Library.akkaActorTyped,
    Library.akkaTypeTest,
    Library.logback,
    Library.scalaLogging,
    Library.scalaTest,
    Library.scalaCheck
  )

  val akkaStreamLibrary = Seq(
    Library.akkaActorTyped,
    Library.akkaStream,
    Library.akkaHttp,
    Library.alpakkaKafka,
    Library.jackson,
    Library.playJson,
    Library.logback,
    Library.scalaLogging,
    Library.akkaTypeTest,
    Library.alpakkaKafkaTest,
    Library.akkaStreamTest,
    Library.scalaTest,
    Library.scalaCheck
  )

  val kafkaLibrary = Seq(
    Library.kafka,
    Library.kafkaClient,
    Library.confluentSerializer,
    Library.scalaLogging,
    Library.scalaTest,
    Library.scalaCheck
  )

  val functionalexercise = Seq(Library.scalaLogging, Library.scalaTest, Library.scalaCheck)
}
