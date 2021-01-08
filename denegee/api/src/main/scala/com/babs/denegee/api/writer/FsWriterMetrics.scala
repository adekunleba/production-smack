package com.babs.denegee.api.writer

case class FsWriterMetrics(
    writerId: String,
    partitionInfo: PartitionIdentifier,
    fileInfo: Iterable[FileInfo]
)
