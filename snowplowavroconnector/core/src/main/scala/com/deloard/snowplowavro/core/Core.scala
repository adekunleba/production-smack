package com.deloard.snowplowavro.core

import java.io.File

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import avrohugger.Generator
import avrohugger.format.SpecificRecord
import org.apache.avro.Schema

trait Core {

  def foo: String = "foo"

}

object Core {

  val coreLogger = Logger(LoggerFactory.getLogger(this.getClass))

  /**
   * Should return a class if exists and and then you can return the class
   * Here is what we need to do with a given avro file.
   *
   * Let avro hugger help you do some generation
   * We will need the fieldname to be able to process the webhook so that we map the fieldname result to the generated
   * case class.
   *
   * We however now wrap this in the Akka http source sink approach.
   *
   * Interestingly you class comes out as a sub Avro Specific Record. I think one can play with this.
   *
   * So when the payload is coming, you basically put each index arising from the record fields and then map the result as
   * the class's case class at then end before now Serializing the case class
   * back to avro for kafka
   */
  def generate(file: File): (List[String], scala.collection.concurrent.Map[String, Schema]) = {
    val generator  = Generator(SpecificRecord)
    val stringList = generator.fileToStrings(file)
//    generator.fileToFile(file)
    val schemaMap = generator.schemaStore.schemas
    coreLogger.info(s"Evaluate generated schema ${schemaMap.keySet}")
    coreLogger.info(s"Generated string is ${stringList.headOption} ")
    (stringList, schemaMap)
  }
}
