package com.babs.denegee.api.utils.io

import java.lang.reflect.ParameterizedType

import com.google.gson.reflect.TypeToken
import com.google.gson.stream.{JsonReader, JsonWriter}
import com.google.gson.{
  Gson,
  GsonBuilder,
  JsonElement,
  JsonNull,
  JsonObject,
  JsonPrimitive,
  TypeAdapter,
  TypeAdapterFactory
}
import com.babs.denegee.common.logging.LoggingAdapter
import com.google.gson.internal.Streams
import com.google.gson.internal.bind.{JsonTreeReader, JsonTreeWriter}

import scala.util.Try

/**
  * Gson interface adapter as a wrapper to read and write data for a subclass of a Trait
  * Scala examples around this that can also be expanded on
  * https://github.com/enterprisedlt/typed-gson/blob/master/src/main/scala/com/github/apolubelov/gson/TypeAwareTypeAdapterFactory.scala
  *https://github.com/augi/gson-scala/blob/master/src/main/scala/cz/augi/gsonscala/OptionalTypeAdapter.scala
  */
//TODO: Look at re-writing this Adapter with basic scala library since what it is trying to do
// is to convert java object of a particular super type to json.
class InterfaceAdapterV2[R](
    gson: Gson,
    typeToken: TypeToken[R],
    nextTypeAdapter: TypeAdapter[R],
    factory: TypeAdapterFactory
) extends TypeAdapter[R]
    with LoggingAdapter {

  val loggingAdapter = logger

  private val OBJECT_TYPE = "object-type"
  private val OBJECT_DATA = "object-data"

  /**
    * Recursively create a json string from a case class
    * @param out
    * @param value
    * @return
    */
  def toJsonElement(out: JsonWriter, value: R): JsonElement =
    value match {
      case x if x == None || x == null =>
        logger.info("Value is none")
        JsonNull.INSTANCE
      case Some(x) =>
        logger.info("Value is optional")
        val jsonTreeWriter = mkTreeWriter(out)
        gson.getAdapter(clazz(x)).write(jsonTreeWriter, x)
        enrichWithTypeOverride(jsonTreeWriter.get(), x)
      case _ =>
        logger.info("Value is normal value")
        val jsonTreeWriter = mkTreeWriter(out)
        gson
          .getDelegateAdapter(factory, TypeToken.get(clazz(value)))
          .write(jsonTreeWriter, value)
        enrichWithTypeOverride(jsonTreeWriter.get(), value)
    }

  override def write(out: JsonWriter, value: R): Unit = {
    val element = toJsonElement(out, value)
    gson.toJson(element, out)
  }

  override def read(jsonReader: JsonReader): R = {
    val jsonElement = Streams.parse(jsonReader)
    //Match over typeToken
    typeToken.getType match {
      case pt: ParameterizedType if pt.getRawType == classOf[Option[_]] =>
        val actualType = pt.getActualTypeArguments()(0)
        val next = gson.getAdapter(TypeToken.get(actualType))
        Option(readJsonElement(jsonReader, jsonElement, next)).asInstanceOf[R]
      case _ =>
        readJsonElement(jsonReader, jsonElement, nextTypeAdapter)
    }
  }

  def readJsonElement[B](
      jsonReader: JsonReader,
      jsonElement: JsonElement,
      next: TypeAdapter[B]
  ): B =
    findTypeOverride(jsonElement)
      .flatMap({ typeOverride =>
        resolveObjectInstance(typeOverride)
          .orElse {
            Option(gson.getAdapter(TypeToken.get(typeOverride))).map({ custom =>
              readJson(jsonElement, jsonReader, custom)
            })
          }
          .map(_.asInstanceOf[B])
      })
      .getOrElse(readJson(jsonElement, jsonReader, next))

  /**
    * Get actual class name of the object
    * @param json
    * @return
    */
  private def findTypeOverride(json: JsonElement): Option[Class[_]] =
    Option(json)
      .filter(_.isJsonObject)
      .flatMap(jo => Option(jo.getAsJsonObject.get(OBJECT_TYPE)))
      .map(_.getAsString)
      .map(Class.forName)
      .filter(realType =>
        typeToken.getType.getTypeName != realType.getCanonicalName)

  private def resolveObjectInstance[X](typeOverride: Class[X]): Option[X] =
    Option(typeOverride)
      .filter(_.getSimpleName.endsWith("$"))
      .flatMap { realType =>
        Try(
          realType.getField("MODULE$").get(realType).asInstanceOf[X]
        ).toOption
      }

  private def readJson[X](json: JsonElement,
                          jsonReader: JsonReader,
                          adapter: TypeAdapter[X]): X = {
    val tr = new JsonTreeReader(json)
    tr.setLenient(jsonReader.isLenient)
    adapter.read(tr)
  }

  // Make the Json Tree writer
  private def mkTreeWriter(jsonWriter: JsonWriter): JsonTreeWriter = {
    val jsonTreeWriter = new JsonTreeWriter
    jsonTreeWriter.setLenient(jsonWriter.isLenient)
    jsonTreeWriter.setHtmlSafe(jsonWriter.isHtmlSafe)
    jsonTreeWriter.setSerializeNulls(jsonWriter.getSerializeNulls)
    jsonTreeWriter
  }

  private def enrichWithTypeOverride[X](jsonValue: JsonElement,
                                        value: X): JsonElement =
    Option(jsonValue)
      .filter(_.isJsonObject)
      .map(_.getAsJsonObject)
      .map { jo =>
        jo.addProperty(OBJECT_TYPE, value.getClass.getName)
        jo
      }
      .getOrElse(jsonValue)

  private def clazz[X](v: X): Class[X] = v.getClass.asInstanceOf[Class[X]]

}

case class GsonInterfaceAdapterV2(baseClass: Class[_])
    extends TypeAdapterFactory {

  override def create[T](gson: Gson, typeToken: TypeToken[T]): TypeAdapter[T] =
    new InterfaceAdapterV2[T](gson,
                              typeToken,
                              gson.getDelegateAdapter(this, typeToken),
                              this)

}

object GsonInterfaceAdapterV2 {
  def apply(baseClass: Class[_]): Gson =
    new GsonBuilder()
      .registerTypeAdapterFactory(new GsonInterfaceAdapterV2(baseClass))
      .create()
}
