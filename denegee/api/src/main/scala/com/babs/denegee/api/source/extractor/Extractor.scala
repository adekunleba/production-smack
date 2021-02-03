package com.babs.denegee.api.source.extractor

import com.babs.denegee.api.records.RecordStreamWithMetadata
import com.babs.denegee.api.stream.{RecordEnvelope, StreamEntity}
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiConsumer
/*
 * A trait for classes that are responsible for extracting data
 * S - Output Schema Type
 * D  - Output Record Type
 */
trait Extractor[S, D] {

  def getSchema: S

  /**
    * Read next data record from data source
    * @param reuse
    */
//  def readRecord(reuse: D)

  def getExpectedRecordCount: Long

  def getHighWatermark: Long

  def recordStream: RecordStreamWithMetadata[D, S]

  def readStreamEntity: StreamEntity[D]

  /**
    * Read record Envelope
    * @return
    */
  def readRecordEnvelope: RecordEnvelope[D]

}
