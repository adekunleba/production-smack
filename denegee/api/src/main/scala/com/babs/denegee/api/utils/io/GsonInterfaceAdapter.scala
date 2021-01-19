package com.babs.denegee.api.utils.io

import java.io.IOException
import java.lang.reflect.ParameterizedType

import cats.syntax.all._
import com.babs.denegee.common.logging.LoggingAdapter
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.{JsonReader, JsonWriter}
import com.google.gson._

/**
  * Gson interface adapter as a wrapper to read and write data for a subclass of a Trait
  * Scala examples around this that can also be expanded on
  * https://github.com/enterprisedlt/typed-gson/blob/master/src/main/scala/com/github/apolubelov/gson/TypeAwareTypeAdapterFactory.scala
  *https://github.com/augi/gson-scala/blob/master/src/main/scala/cz/augi/gsonscala/OptionalTypeAdapter.scala
  */
//TODO: Look at re-writing this Adapter with basic scala library since what it is trying to do
// is to convert java object of a particular super type to json.
class InterfaceAdapter[R](gson: Gson,
                          factory: TypeAdapterFactory,
                          typeToken: TypeToken[R])
    extends TypeAdapter[R]
    with LoggingAdapter {

  val loggingAdapter = logger

  private val OBJECT_TYPE = "object-type"
  private val OBJECT_DATA = "object-data"
  override def write(out: JsonWriter, value: R): Unit = {
    loggingAdapter.info(s"Checking if logger adapter works")
    if (classOf[Option[_]].isAssignableFrom(typeToken.getRawType)) {
      val option = value.some
      if (option.isDefined) {
        writeObject(option.get, out)
      } else {
        out.beginObject()
        out.endObject()
      }

    } else {
      writeObject(value, out)
    }
  }

  override def read(in: JsonReader): R = {
    // Is there an advantage of using `JsonParser.parse(in)` compared to Stream?
    val json = Streams.parse(in)
    loggingAdapter.info("Json successfully parsee")

    // Manage option Type
    typeToken.getType match {
      case _ if json.isJsonNull => {
        loggingAdapter.warn("Passed json is empty")
        readNull()
      }
      case pt: ParameterizedType if pt.getRawType == classOf[Option[_]] =>
        val jsonObject = json.getAsJsonObject
        readValue(jsonObject, null).some.asInstanceOf[R]
      case _ =>
        loggingAdapter.info("Non optional reading is chosen")
        val jsonObject = json.getAsJsonObject
        readValue(jsonObject, typeToken)
    }
    json.asInstanceOf[R]
  }

  private[io] def writeObject[S](value: S, out: JsonWriter): Unit = {
    val jsonObjet = new JsonObject()
    jsonObjet.add(OBJECT_TYPE, new JsonPrimitive(value.getClass.getName))
    val delegate: TypeAdapter[S] =
      gson.getDelegateAdapter(factory, TypeToken.get(clazz(value)))
    jsonObjet.add(OBJECT_DATA, delegate.toJsonTree(value))
    Streams.write(jsonObjet, out)
  }

  private def readNull() =
    if (typeToken.getRawType == classOf[Option[_]]) {
      None
    } else {
      null
    }

  private def readValue[S](jsonObject: JsonObject,
                           defaultTypeToken: TypeToken[S]) =
    if (jsonObject.isJsonNull()) {
      null
    } else if (jsonObject.has(OBJECT_TYPE)) {
      val className = jsonObject.get(OBJECT_TYPE).getAsString
      // Make a class of the classname
      val clazz = Class.forName(className)
      //TODO: you can use scala reflection to instantiate the class
      completeDelegation(jsonObject, TypeToken.get(clazz))
    } else if (defaultTypeToken != null) {
      completeDelegation(jsonObject, defaultTypeToken)
    } else {
      throw new IOException("Could not determine typetoken")
    }

  private def completeDelegation[S](jsonObject: JsonObject,
                                    token: TypeToken[S]) = {
    val delegate: TypeAdapter[S] = gson.getDelegateAdapter(factory, token)
    delegate.fromJsonTree(jsonObject.get(OBJECT_DATA))
  }

  private def clazz[X](v: X): Class[X] = v.getClass.asInstanceOf[Class[X]]

}

case class GsonInterfaceAdapter(baseClass: Class[_])
    extends TypeAdapterFactory {

  override def create[T](gson: Gson, typeToken: TypeToken[T]): TypeAdapter[T] =
    /**
      * TODO: Delectages other data types:
      *- Primitives and boxed primitives
      * *   - Arrays
      * *   - Collections
      * *   - Maps
      *
      * Check the java GsonIntefaceAdapter approach
      */
    // RawType looks for the supertype of the typeToken
    if (baseClass.isAssignableFrom(typeToken.getRawType)) {
      new InterfaceAdapter(gson, this, typeToken)
    } else {
      // Let the default Gson writer manage other classes
      null
    }

}

object GsonInterfaceAdapter {
  def apply(baseClass: Class[_]): Gson =
    new GsonBuilder()
      .registerTypeAdapterFactory(new GsonInterfaceAdapter(baseClass))
      .create()
}
