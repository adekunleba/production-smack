import sbt._

object Dependencies {

  lazy val Name = new {
    val circe      = "io.circe"
    val prometheus = "io.prometheus"
    val typesafe   = "com.typesafe.akka"
    val typelevel  = "org.typelevel"
  }

  lazy val Version = new {
    val akka           = "2.6.10"
    val akkaHttp       = "10.2.1"
    val alpakka        = "2.0.6"
    val config         = "1.3.3"
    val circe          = "0.9.3"
    val catCore        = "2.2.0"
    val enumeratum     = "1.6.1"
    val googleGson     = "2.8.6"
    val hadoopVersion  = "2.7.3"
    val logback        = "1.2.3"
    val prometheus     = "0.5.0"
    val reflection     = "0.9.12"
    val scala          = "2.12.7"
    val scalaLogging   = "3.9.0"
    val scopt          = "3.7.0"
    val scalaArm       = "2.0"
    val scalatest      = "3.1.0"
    val scalamock      = "3.6.0"
    val scalacheck     = "1.13.4"
    val scalaShapeless = "1.2.3"
    val gatling        = "2.3.1"
    val sprayJson      = "1.3.6"
  }

  lazy val commonDependencies = Seq(
    "ch.qos.logback"             % "logback-classic"              % Version.logback,
    "com.typesafe.scala-logging" %% "scala-logging"               % Version.scalaLogging,
    "com.typesafe"               % "config"                       % Version.config,
    Name.typelevel               %% "cats-core"                   % Version.catCore,
    "org.scalatest"              %% "scalatest"                   % Version.scalatest % Test,
    "org.scalamock"              %% "scalamock-scalatest-support" % Version.scalamock % Test,
    "org.scalacheck"             %% "scalacheck"                  % Version.scalacheck % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14"   % Version.scalaShapeless % Test
  )

  lazy val appDependencies = commonDependencies ++ Seq(
    Name.typesafe   %% "akka-actor-typed"         % Version.akka,
    Name.typesafe   %% "akka-stream"              % Version.akka,
    Name.typesafe   %% "akka-http"                % Version.akkaHttp,
    Name.typesafe   %% "akka-slf4j"               % Version.akka,
    Name.circe      %% "circe-core"               % Version.circe,
    Name.circe      %% "circe-generic"            % Version.circe,
    Name.circe      %% "circe-parser"             % Version.circe,
    Name.circe      %% "circe-java8"              % Version.circe,
    Name.prometheus % "simpleclient"              % Version.prometheus,
    Name.prometheus % "simpleclient_common"       % Version.prometheus,
    Name.prometheus % "simpleclient_hotspot"      % Version.prometheus,
    Name.typesafe   %% "akka-actor-testkit-typed" % Version.akka % Test,
    Name.typesafe   %% "akka-http-testkit"        % Version.akkaHttp % Test,
    Name.typesafe   %% "akka-stream-testkit"      % Version.akka % Test
  )

  lazy val cliDependencies = commonDependencies ++ Seq(
    "com.github.scopt" %% "scopt" % Version.scopt
  )

  lazy val perfDependencies = commonDependencies ++ Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % Version.gatling % "test,it",
    "io.gatling"            % "gatling-test-framework"    % Version.gatling % "test,it"
  )

  lazy val apiDependencies = commonDependencies ++ Seq(
    Name.typesafe          %% "akka-actor-typed" % Version.akka,
    Name.typesafe          %% "akka-stream"      % Version.akka,
    "com.beachape"         %% "enumeratum"       % Version.enumeratum,
    "io.spray"             %% "spray-json"       % Version.sprayJson,
    "com.google.code.gson" % "gson"              % Version.googleGson,
    "org.reflections"      % "reflections"       % Version.reflection,
    "org.apache.hadoop"    % "hadoop-client"     % Version.hadoopVersion,
    "com.jsuereth"         %% "scala-arm"        % Version.scalaArm
  )

}
