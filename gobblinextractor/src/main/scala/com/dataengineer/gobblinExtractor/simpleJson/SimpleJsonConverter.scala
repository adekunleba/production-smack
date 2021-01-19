package com.dataengineer.gobblinExtractor.simpleJson

import java.lang

import com.google.gson.reflect.TypeToken
import com.google.gson.{Gson, JsonElement}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.gobblin.configuration.WorkUnitState
import org.apache.gobblin.converter.{
  SchemaConversionException,
  SingleRecordIterable,
  ToAvroConverterBase
}

class SimpleJsonConverter extends ToAvroConverterBase[String, String] {

  private val gson = new Gson()

  @throws(classOf[SchemaConversionException])
  def convertSchema(schema: String, workUnit: WorkUnitState): Schema = {
    new Schema.Parser().parse(schema)
  }

  def convertRecord(outputSchema: Schema,
                    inputRecord: String,
                    workUnit: WorkUnitState): lang.Iterable[GenericRecord] = {
    val FIELD_ENTRY_TYPE = new TypeToken[Map[String, Object]]() {}.getType
    val element = gson.fromJson(inputRecord, classOf[JsonElement])
    val fields = gson.fromJson[Map[String, Object]](element, FIELD_ENTRY_TYPE)
    val record = new GenericData.Record(outputSchema)
    fields.foreach({
      case (key, value) => record.put(key, value)
    })
    new SingleRecordIterable[GenericRecord](record)
  }
}
