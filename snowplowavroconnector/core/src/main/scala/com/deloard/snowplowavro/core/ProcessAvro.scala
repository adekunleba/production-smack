package com.deloard.snowplowavro.core

import avrohugger.format.SpecificRecord
import avrohugger.input.NestedSchemaExtractor
import avrohugger.matchers.TypeMatcher
import avrohugger.stores.SchemaStore
import avrohugger.types.AvroScalaTypes
import org.apache.avro.Schema

/**
 * Process Avro files should be able to put the data given a schema string
 *
 * From the schema string one should get the field names and object class
 * as well a
 *
 * So get the fieldnames as Map of index to name.
 *
 * @param avroSchema
 */
class ProcessAvro(avroSchema: org.apache.avro.specific.SpecificRecordBase) {

  /**
   * Get the field items with their index and name.
   */
  def getFieldList(): Map[Int, String] = {

    val schema: Schema                                = avroSchema.getSchema
    val avroScalaCustomTypes: Option[AvroScalaTypes]  = None
    val avroScalaCustomNamespace: Map[String, String] = Map.empty
    val schemaStore                                   = new SchemaStore
    val typeMatcher = new TypeMatcher(
      avroScalaCustomTypes.getOrElse(SpecificRecord.defaultTypes),
      avroScalaCustomNamespace
    )
//    schema.getType match
    val schemaList = NestedSchemaExtractor.getNestedSchemas(schema, schemaStore, typeMatcher)
    Map[Int, String]()
  }

}
