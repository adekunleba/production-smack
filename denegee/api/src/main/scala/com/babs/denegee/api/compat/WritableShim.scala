package com.babs.denegee.api.compat

import java.io.{DataInput, DataOutput}

/**
  * A mirror for Hadoop's Writable Interface
  * Basically allows object to implement similar serializers
  * without explicitly depending on Hadoop
  */
trait WritableShim {

  def readFields(dataInput: DataInput): Boolean
  def writeFields(dataOutput: DataOutput): Boolean
}
