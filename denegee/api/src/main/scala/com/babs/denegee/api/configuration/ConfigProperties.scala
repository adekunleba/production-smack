package com.babs.denegee.api.configuration

import com.babs.denegee.common.logging.LoggingAdapter

import scala.collection.mutable

/**
  * The reason I think I must be getting a stackoverflow with the implementation I had earlier
  * For props State may largely be due to synchronization since HashMap is synchronized by default
  */
case class ConfigProperties[K, V](props: mutable.HashMap[K, V])
    extends LoggingAdapter {
  self =>

  val configLogger = logger
//  private lazy val defaultConfig: ConfigProperties[K, V] =
//    ConfigProperties.empty[K, V]

  def setProperty(key: K, value: V): Unit =
    self.props.update(key, value)

  def getProperty(key: K): Option[V] = self.props.get(key)

  //  def getAllProps = self.defaultConfig.

  def putAll(config: ConfigProperties[K, V]): Unit =
    config.getprops
      .foreach({
        case (k, v) => self.props.update(k, v)
      })

  def getprops: Map[K, V] = self.props.toMap

  def isEmpty: Boolean = self.props.isEmpty
}
object ConfigProperties {

  def apply[K, V](props: ConfigProperties[K, V]): ConfigProperties[K, V] =
    new ConfigProperties[K, V](props.props)

  def apply[K, V](props: Map[K, V]): ConfigProperties[K, V] =
    new ConfigProperties(mutable.HashMap(props.toSeq: _*))

  def apply[K, V](): ConfigProperties[K, V] =
    new ConfigProperties[K, V](mutable.HashMap.empty[K, V])

  def apply[K, V](
      defaultEntryValue: mutable.HashMap[K, V]
  ): ConfigProperties[K, V] =
    new ConfigProperties[K, V](defaultEntryValue)

  def empty[K, V]: ConfigProperties[K, V] = ConfigProperties()
}
