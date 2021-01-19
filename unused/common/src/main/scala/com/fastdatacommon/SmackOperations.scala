package com.fastdatacommon

trait SmackOperations[T] {

/***
    * Stateless method getData for the various engines of the SMACK Stack
    * It should help retrieve data from defined source
    * @return
    */
  def getData(source: T): T

  /**
   * Stateless method process Data for the various engines of the SMACK stack
   * It should be implemented to do different sets of processing as required by the
   * engine of interest
   * @param data
   * @return
   */
  def process(data: T): T

  /**
   * Stateless method to extract data for the various engines of the SMACK stack
   * It should be implemented to extract data differently for any of the various engines or
   * it's various implementation sources.
   * @param sourceData
   * @return
   */
  def extractData(sourceData: T): T

  /**
   * Stateless method to monitor the data throughput into the system, control it so that we don't
   * have backpressure for the various engines that make up our Smack engine
   * It should be implemented for the various engines in the SMACK stack
   * @param engine
   * @return
   */
  def checkBackPressure(engine: T): T

  /**
   * Stateless method to persist the data to any destination from any of the source engines
   * It should be implemented to perist data for any of the engine in the SMACK stack.
   * @param source
   * @return
   */
  def persist(source: T): T

  /**
   * A persist later stateless method that works like persist just not running immediately based
   * on defined terms
   * @return
   */
  def persistLater(source: T): T

}