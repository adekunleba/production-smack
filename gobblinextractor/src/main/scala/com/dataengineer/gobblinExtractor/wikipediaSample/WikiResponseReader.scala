package com.dataengineer.gobblinExtractor.wikipediaSample

import com.google.gson.JsonElement

class WikiResponseReader extends Iterator[JsonElement] {
  override def hasNext: Boolean = ???

  override def next(): JsonElement = ???
}
