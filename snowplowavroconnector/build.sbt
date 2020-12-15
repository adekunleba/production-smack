import Dependencies._
import Settings._

lazy val core = (project in file("core"))
  .settings(Settings.settings: _*)
  .settings(Settings.coreSettings: _*)
  .settings(Settings.formatting: _*)
  .settings(libraryDependencies ++= coreDependencies)

lazy val kafka = (project in file("kafka"))
  .settings(Settings.settings: _*)
  .settings(Settings.kafkaSettings: _*)
  .settings(libraryDependencies ++= kafkaDependencies)
lazy val avroschema = (project in file("avroschema"))
  .settings(Settings.settings: _*)
  .settings(libraryDependencies ++= kafkaDependencies)
lazy val snowplowavro = (project in file("snowplowavro"))
  .settings(Settings.settings: _*)
  .settings(Settings.snowplowavroSettings: _*)
  .settings(libraryDependencies ++= snowplowavroDependencies)
  .dependsOn(core, kafka)
  .configs(Test)
