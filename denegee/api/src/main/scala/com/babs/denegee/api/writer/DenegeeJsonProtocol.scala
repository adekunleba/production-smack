package com.babs.denegee.api.writer

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object DenegeeJsonProtocol extends DefaultJsonProtocol {

  implicit val partitionIdentifierFormat: RootJsonFormat[PartitionIdentifier] =
    jsonFormat2(
      PartitionIdentifier
    )
  implicit val fileInfoFormat: RootJsonFormat[FileInfo] = jsonFormat2(FileInfo)
  implicit val fsWriterMetricFormat: RootJsonFormat[FsWriterMetrics] =
    jsonFormat3(FsWriterMetrics)
}
