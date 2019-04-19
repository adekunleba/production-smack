name := "Prod Smack"
organization in ThisBuild := "com.fastdatacommon"
scalaVersion in ThisBuild   := "2.12.7"


lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion    = "2.5.22"


//PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    akkaserver
  )
//TO Aggregate with global

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )


lazy val akkaserver = project
  .settings(
    name := "akkaserver",
    settings,
    libraryDependencies ++= Dependencies.akkaDependencies
      ++ commonDependencies
  )


lazy val settings =
  commonSettings //++ wartremoverSettings

//COmpiler Options as Sequence passed to commonSettings
lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-Xfatal-warnings",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding", "utf8"
)


//Common settings give the genra
lazy val commonSettings = Seq (
  scalacOptions ++= compilerOptions,
  //Resolvers passed to commonSettings
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
    )
  )

//lazy val wartremoverSettings = Seq(
//  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
//)

/**
  * Common Dependencies should be added to all project wide.
  */
lazy val commonDependencies = Seq(

  "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
  "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,

  //logging
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback"             %  "logback-classic"             % "1.2.3",
  "com.typesafe"                % "config"        % "1.3.3",
)
