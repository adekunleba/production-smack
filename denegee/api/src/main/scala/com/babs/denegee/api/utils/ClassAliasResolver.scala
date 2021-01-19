package com.babs.denegee.api.utils

import com.babs.denegee.reflect.{Alias, DoNotScan}
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.util.Try
import cats.syntax.all._
import com.babs.denegee.common.config.CommonSettings

/**
  * Define a class to resolve subtypes that are marked with `Alias` annotation.
  * The class search the classpath using reflection and look for packages that are
  * annotated with Alias and belong to a subtype of T
  * @tparam T
  */
class ClassAliasResolver[T](subTypeOf: Class[T]) {

  import com.babs.denegee.common.config.CommonSettingsImplicits._

  private val aliasToClassCache = mutable.HashMap.empty[String, Class[_ <: T]]

  /**
    * A list of package names to scan. Ideally should be loaded from the configuration file
    * Gobblin provides some default but hydra gave an idea of requesting them through
    * a configuration file on start of application.
    */
  private[utils] val commonConfig = CommonSettings.applicationConfig
  private[utils] val packagesToScan
    : List[String] = "com.babs.denegee" +: commonConfig
    .getOptionalStrList("scan-packages")
    .getOrElse(List.empty)

  // This builder basically intends to scan for subtypes of a class
  private def reflectionConfig =
    new ConfigurationBuilder()
      .forPackages(packagesToScan: _*)
  //.addScanners(new SubTypesScanner)
  //.useParallelExecutor()

  private var _reflectionCfg = new Reflections(reflectionConfig)

  /**
    * Get subtypes of a subtype and ensure classes that should not be scanned
    * is not scanned
    */
  private val subTypeList = _reflectionCfg
    .getSubTypesOf(subTypeOf)
    .asScala
    .filterNot(_.isAnnotationPresent(classOf[DoNotScan]))

  /**
    * Build the subTypeList Cache on class initialization
    */
  subTypeList
    .filter(_.isAnnotationPresent(classOf[Alias]))
    .foreach({ clazz =>
      val aliasObject: Alias = clazz.getAnnotation(classOf[Alias])
      val alias = aliasObject.value().toUpperCase()
      if (!aliasToClassCache.contains(alias)) {
        aliasToClassCache.put(alias, clazz)
      }
    })

  def resolve(possibleAlias: String): String =
    aliasToClassCache
      .get(possibleAlias.toUpperCase)
      .map(_.getName)
      .getOrElse(possibleAlias)

  /**
    * Resolve be creating class from alias or class name
    * @param aliasOrClass
    * @return
    */
  def resolveClass(aliasOrClass: String): Option[Class[_ <: T]] =
    if (aliasToClassCache.contains(aliasOrClass.toUpperCase)) {
      aliasToClassCache.get(aliasOrClass.toUpperCase)
    } else {
      Try(Class.forName(aliasOrClass).asSubclass(subTypeOf)).toOption
    }

  /**
    * Get the package map in the immutable format
    * @return Map of alias value and the class object
    */
  private[utils] def packageMap: Map[String, Class[_ <: T]] =
    aliasToClassCache.toMap

}

object ClassAliasResolver {

  def apply[T](subTypeOf: Class[T]): ClassAliasResolver[T] =
    new ClassAliasResolver[T](subTypeOf)
}
