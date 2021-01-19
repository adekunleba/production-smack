package com.babs.denegee.api.compat

import java.io.{
  ByteArrayInputStream,
  ByteArrayOutputStream,
  DataInputStream,
  DataOutputStream
}

import com.babs.denegee.api.test.RandomGenerator
import org.apache.hadoop.io.Text
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers
import resource._

class TextSerializerTest
    extends Matchers
    with AnyFunSpecLike
    with RandomGenerator {

  describe("When using TextSerializer") {
    it("should properly serializer and work with hadoop") {
      val textsToSerializer = random[List[String]]

      textsToSerializer.foreach { text =>
        val byteArray =
          for (out <- managed(new ByteArrayOutputStream()))
            yield {
              val dout = new DataOutputStream(out)
              TextSerializer.writeStringAsText(dout, text)
              out.toByteArray
            }
        byteArray.opt.isDefined shouldBe true
        val array = byteArray.opt.get
        val inData =
          for (in <- managed(new ByteArrayInputStream(array)))
            yield {
              val dIn = new DataInputStream(in)
              dIn
            }
        assert(inData.opt.isDefined)
        val hadoopText = new Text()
        hadoopText.readFields(inData.opt.get)
        hadoopText.toString shouldBe text
      }
    }

    it("should properly deserializer hadoop data") {

      val textsToSerializer = random[List[String]]

      textsToSerializer.foreach { text =>
        val byteArray =
          for (out <- managed(new ByteArrayOutputStream()))
            yield {
              val dout = new DataOutputStream(out)
              TextSerializer.writeStringAsText(dout, text)
              val hadoopText = new Text()
              hadoopText.set(text)
              hadoopText.write(dout)
              out.toByteArray
            }
        byteArray.opt.isDefined shouldBe true
        val inData =
          for (in <- managed(new ByteArrayInputStream(byteArray.opt.get)))
            yield {
              val dIn = new DataInputStream(in)
              dIn
            }
        assert(inData.opt.isDefined)
        val deserialized = TextSerializer.readTextAsString(inData.opt.get)
        deserialized shouldBe text
      }
    }
  }
}
