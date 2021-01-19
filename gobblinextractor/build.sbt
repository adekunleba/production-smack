lazy val akkaHttpVersion = "10.2.1"
lazy val akkaVersion = "2.6.10"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(organization := "com.dataengineer", scalaVersion := "2.12.8")
    ),
    name := "gobblinExtractor",
    resolvers ++= Seq(
      DefaultMavenRepository,
      Resolver.bintrayRepo("typesafe", "releases"),
      Resolver.sonatypeRepo("releases"),
      "Confluent Platform" at "http://packages.confluent.io/maven/",
      "Apache Releases" at "https://repository.apache.org/content/repositories/releases/",
      "Apache Snapshots" at "https://repository.apache.org/content/repositories/snapshots/",
      "Other Maven" at "http://conjars.org/repo/"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.apache.gobblin" % "gobblin-api" % "0.14.0",
      "org.apache.gobblin" % "gobblin-core" % "0.14.0",
      "org.apache.commons" % "commons-vfs2" % "2.6.0",
//    "org.apache.gobblin" % "gobblin-http" % "0.14.0",
//    "org.apache.gobblin" % "gobblin-parquet" % "0.14.0",
//    "org.apache.gobblin" % "gobblin-data-management" % "0.14.0",
      "com.jsuereth" %% "scala-arm" % "2.0",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    assemblyJarName in assembly := "dataengineer-gobblin.jar",
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs @ _*)    => MergeStrategy.last
      case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "MANIFEST.MF")      => MergeStrategy.discard
      case PathList("META-INF", xs @ _*)            => MergeStrategy.last
      case PathList(ps @ _*)
          if ps.exists(_ contains "javax") || ps.exists(_ contains "sun") =>
        MergeStrategy.last
      case PathList(ps @ _*)
          if ps.last.contains(".html") || ps.last.contains(".xml") =>
        MergeStrategy.last
      case PathList(ps @ _*)
          if ps.exists(_ contains "codehaus") || ps
            .exists(_ contains "groovy") || ps
            .exists(_ contains "stringtemplate") || ps
            .exists(_ contains "antlr") =>
        MergeStrategy.last
      case PathList(ps @ _*)
          if ps.exists(_ contains "hadoop") || ps
            .exists(_ contains "logging") || ps.exists(_ contains "slf4j") =>
        MergeStrategy.first
      case "application.conf" => MergeStrategy.concat

      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
