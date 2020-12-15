package com.dataengineer.gobblinExtractor.simpleJson

import java.util

import org.apache.gobblin.configuration.{
  ConfigurationKeys,
  SourceState,
  WorkUnitState
}
import org.apache.gobblin.source.Source
import org.apache.gobblin.source.extractor.Extractor
import org.apache.gobblin.source.workunit.{Extract, WorkUnit}

import scala.collection.JavaConverters._

class SimpleJsonSource extends Source[String, String] {

  private val SOURCE_FILE_KEY = "source.file"
  override def getWorkunits(state: SourceState): util.List[WorkUnit] = {

    if (!state.contains(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL))
      List.empty[WorkUnit].asJava
    else {

      val extract = new Extract(
        Extract.TableType.SNAPSHOT_ONLY,
        state.getProp(
          ConfigurationKeys.EXTRACT_NAMESPACE_NAME_KEY,
          "ExampleNameSpace"
        ),
        "ExampleTable"
      )
      val units = state
        .getProp(ConfigurationKeys.SOURCE_FILEBASED_FILES_TO_PULL)
        .split(",")
        .filterNot(_.isEmpty)
        .foldRight(List.empty[WorkUnit]) {
          case (file, ls) =>
            val workUnit = WorkUnit.create(extract)
            workUnit.setProp(SOURCE_FILE_KEY, file)
            workUnit :: ls
        }
      units.asJava
    }
  }

  override def getExtractor(state: WorkUnitState): Extractor[String, String] =
    SimpleJsonExtractor(state)

  override def shutdown(state: SourceState): Unit = ???
}
