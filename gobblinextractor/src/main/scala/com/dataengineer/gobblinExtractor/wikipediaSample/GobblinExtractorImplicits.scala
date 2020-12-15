package com.dataengineer.gobblinExtractor.wikipediaSample

object GobblinExtractorImplicits {

  def throwableLeft[T](block: => T): Either[java.lang.Throwable, T] = {
    try {
      Right(block)
    } catch {
      case ex: Throwable => Left(ex)
    }
  }
}
