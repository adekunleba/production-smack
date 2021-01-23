import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._
//import sbtavrohugger.SbtAvrohugger.autoImport._

object Settings {

  private lazy val resolverSites = Seq(
    "Confluent Maven Repo".at("https://packages.confluent.io/maven/"),
    Resolver.sonatypeRepo("releases")
  )

  lazy val settings = Seq(
    organization := "com.akkaexamples.babsde",
    version := "0.0.1" + sys.props.getOrElse("buildNumber", default = "0-SNAPSHOT"),
    scalaVersion := "2.12.7",
    publishMavenStyle := true,
    publishArtifact in Test := false,
    scalacOptions ++= Seq("-deprecation"),
    resolvers ++= resolverSites
  )

  lazy val testSettings =
    Seq(fork in Test := true, parallelExecution in Test := false)

  lazy val itSettings = Defaults.itSettings ++ Seq(
    logBuffered in IntegrationTest := false,
    fork in IntegrationTest := true
  )

  lazy val snowplowavroSettings = Seq(
    assemblyJarName in assembly := "snowplowavro-" + version.value + ".jar",
    test in assembly := {},
    target in assembly := file(baseDirectory.value + "/../bin/"),
    assemblyOption in assembly := (assemblyOption in assembly)
      .value
      .copy(includeScala = false, includeDependency = true),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*)       => MergeStrategy.discard
      case n if n.startsWith("reference.conf") => MergeStrategy.concat
      case _                                   => MergeStrategy.first
    }
  )

  lazy val coreSettings = Seq()

  lazy val akkaCoreSettings = Seq()

  lazy val akkaStreamSettings = Seq()

  lazy val kafkaModulesSetting = Seq()

//  lazy val exampleProjectSettings = Seq()

  // Scalafmt plugin
  import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
  lazy val formatting = Seq(
    scalafmtConfig := file(".scalafmt.conf"),
    scalafmtOnCompile := true
  )
}
