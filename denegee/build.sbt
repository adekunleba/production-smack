import Settings._

lazy val common = project.in(file("common")).settings(commonSettings)

lazy val app = project.in(file("app")).enablePlugins(JavaServerAppPackaging).settings(appSettings).dependsOn(common)

lazy val cli = project.in(file("cli")).settings(cliSettings).dependsOn(common)

lazy val perf = project.in(file("perf")).enablePlugins(GatlingPlugin).settings(perfSettings).dependsOn(common)

// Base replica for gobblin api - Gobblin api is the foundation for many of the other gobblin module
lazy val api = project.in(file("api")).settings(apiSettings).dependsOn(common)

lazy val `denegee` = project
  .in(file("."))
  // TODO issue AutomateHeaderPlugin https://github.com/sbt/sbt-header/issues/153
  .enablePlugins(SiteScaladocPlugin, ScalaUnidocPlugin)
  .settings(rootSettings)
  .aggregate(common, app, cli, perf, api)
