package com.babs.denegee.api.writer

import com.babs.denegee.api.test.RandomGenerator
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

class DenegeeJsonProtocolSpec
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {
  import DenegeeJsonProtocol._
  import spray.json._

  describe("When using DenegeeJsonProtocol") {
    it("should convert Fileinfo to json") {
      val fileInfo = random[FileInfo]
      val fileInfoJson = fileInfo.toJson
      val recoveredFileInfo = fileInfoJson.convertTo[FileInfo]
      recoveredFileInfo shouldBe fileInfo
      recoveredFileInfo.fileName shouldBe fileInfo.fileName
      recoveredFileInfo.numRecords shouldBe fileInfo.numRecords
    }

    it("should convert Fileinfo list to json") {
      val fileInfo1 = random[FileInfo]
      val fileInfo2 = random[FileInfo]
      val fileInfoJson = Seq(fileInfo1, fileInfo2).toJson
      val recoveredFileInfo = fileInfoJson.convertTo[Iterable[FileInfo]]
      recoveredFileInfo shouldBe Seq(fileInfo1, fileInfo2)
      recoveredFileInfo.size shouldBe 2
    }
    it("should convert PartitionIdentifier to json") {
      val partitionIdentifier = random[PartitionIdentifier]
      val pIJson = partitionIdentifier.toJson
      val recoveredPI = pIJson.convertTo[PartitionIdentifier]
      recoveredPI shouldBe partitionIdentifier
      recoveredPI.branchId shouldBe partitionIdentifier.branchId
      recoveredPI.partitionKey shouldBe partitionIdentifier.partitionKey

    }
  }
}
