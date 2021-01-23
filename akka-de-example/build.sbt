lazy val core = (project in file("core"))
  .settings(Settings.settings: _*)
  .settings(Settings.coreSettings: _*)
  .settings(libraryDependencies ++= Dependencies.coreLibrary)

lazy val akkacore = (project in file("akkacore"))
  .settings(name := "akkacore")
  .settings(Settings.settings: _*)
  .settings(Settings.akkaCoreSettings: _*)
  .settings(libraryDependencies ++= Dependencies.akkaCoreLibrary)

lazy val akkastream = (project in file("akkastream"))
  .settings(name := "akkastream")
  .settings(Settings.settings: _*)
  .settings(Settings.akkaStreamSettings: _*)
  .settings(libraryDependencies ++= Dependencies.akkaStreamLibrary)

lazy val kafkamodules = (project in file("kafkamodules"))
  .settings(name := "kafkamodules")
  .settings(Settings.settings: _*)
  .settings(Settings.kafkaModulesSetting: _*)
  .settings(libraryDependencies ++= Dependencies.kafkaLibrary)

lazy val functionalexercise = (project in file("functionalexercise"))
  .settings(name := "functionalexercise")
  .settings(Settings.settings: _*)
  .settings(libraryDependencies ++= Dependencies.functionalexercise)

lazy val main = (project in file("main")).settings(name := "main").dependsOn(core, akkacore, akkastream, kafkamodules)

lazy val akkaDeExample = (project in file(".")).aggregate(main, core, akkacore, akkastream, kafkamodules)
