import sbt._
import Keys._

object Dependencies {
  lazy val resolutionRepos = Seq(
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
  )
  object Version {
    //Java Libraries

    // Scala Libraries
    lazy val akkaHttpVersion = "10.2.1"
    lazy val akkaVersion = "2.6.10"
    lazy val LogbackVersion = "1.2.3"
    lazy val avroHugger = "1.0.0-RC22"

    // Test Libraries
    val scalaTest = "3.0.7"
    val scalaCheck = "1.13.4"

  }

  object Library {
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.akkaHttpVersion
    val akkaHttpJson = "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttpVersion
    val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % Version.akkaVersion
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akkaVersion
    val logback = "ch.qos.logback" % "logback-classic" % Version.LogbackVersion exclude ("org.slf4j", "slf4j-api")
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    val akkaHttpTest = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttpVersion % Test
    val akkaTypeTest = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akkaVersion % Test
    val test = "org.scalatest" %% "scalatest" % Version.scalaTest % Test
    val check = "org.scalacheck" %% "scalacheck" % Version.scalaCheck % Test
    val avroHuggerCore = "com.julianpeeters" %% "avrohugger-core" % Version.avroHugger
    val avroHuggerFileSorter = "com.julianpeeters" %% "avrohugger-filesorter" % Version.avroHugger
  }

  val coreDependencies: Seq[ModuleID] = Seq(
    Library.akkaHttp,
    Library.akkaActorTyped,
    Library.akkaHttpJson,
    Library.akkaStream,
    Library.logback,
    Library.scalaLogging,
    Library.avroHuggerCore,
    Library.avroHuggerFileSorter,
    Library.test,
    Library.check,
    Library.akkaHttpTest,
    Library.akkaTypeTest
  )

  val kafkaDependencies: Seq[ModuleID] = Seq(Library.test, Library.check)

  val snowplowavroDependencies: Seq[ModuleID] = Seq(Library.test, Library.check)

}
