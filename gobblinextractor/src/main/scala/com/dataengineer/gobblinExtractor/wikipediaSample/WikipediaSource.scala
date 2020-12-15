package com.dataengineer.gobblinExtractor.wikipediaSample

import java.util

import com.google.gson.JsonElement
import org.apache.gobblin.configuration.{SourceState, WorkUnitState}
import org.apache.gobblin.source.extractor.Extractor
import org.apache.gobblin.source.extractor.extract.AbstractSource
import org.apache.gobblin.source.workunit.WorkUnit

class WikipediaSource extends AbstractSource[String, JsonElement] {
  override def getWorkunits(state: SourceState): util.List[WorkUnit] = ???

  override def getExtractor(
    state: WorkUnitState
  ): Extractor[String, JsonElement] =
    WikipediaExtractor(state) // Uses the object apply method

  override def shutdown(state: SourceState): Unit = ???
}
