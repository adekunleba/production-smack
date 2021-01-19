package com.babs.denegee.api.writer

import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class FsWriterMetricsTestSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using FsWriterMetrics") {
    it("should serialize the class to json string") {
      import DenegeeJsonProtocol._
      import spray.json._
      val writerId = random[String]
      val partitionInfo = random[PartitionIdentifier]
      val fileInfo1 = random[FileInfo]
      val fileInfo2 = random[FileInfo]
      val fileInfo: Iterable[FileInfo] = Seq(fileInfo1, fileInfo2)
      val fileWriter =
        FsWriterMetrics(writerId, partitionInfo, fileInfo)
      val jsonFileWriter: JsValue = fileWriter.toJson
      val recoveredFsWriter = jsonFileWriter.convertTo[FsWriterMetrics]

      fileWriter shouldBe recoveredFsWriter
      recoveredFsWriter.writerId shouldBe writerId
      recoveredFsWriter.partitionInfo shouldBe partitionInfo
      recoveredFsWriter.fileInfo shouldBe Seq(fileInfo1, fileInfo2)
    }

  }
}
