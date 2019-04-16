import sbt._
import Versions._

object Dependencies {


  val akkaHttp = "com.typesafe.akka" %% "akka-http"  % akkaHttpVersion
  val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion








  //Bringing together all dependencies

  lazy val akkaDependencies = Seq(akkaHttp, akkaHttpSprayJson)

}