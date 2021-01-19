package com.babs.denegee.common.config

import com.typesafe.config.{Config, ConfigFactory}
import cats.syntax.all._
object CommonSettings {
  private[this] lazy val config = ConfigFactory.load()
  private[this] val commonConfig = config.getConfig("common")
  private[this] val logConfig = commonConfig.getConfig("log")

  val name: String = commonConfig.getString("application.name")
  val logName: String = logConfig.getString("name")
  val applicationConfig = commonConfig.getConfig(name)
}

object CommonSettingsImplicits {
  import scala.collection.JavaConverters._

  implicit class ConfigImplicits(config: Config) {

    def getOptionalStrList(path: String): Option[List[String]] =
      getOptional(path, config.getStringList).map(_.asScala.toList)

    private def getOptional[A](path: String, method: String => A): Option[A] =
      if (config.hasPath(path)) {
        method(path).some
      } else {
        none
      }
  }
}
