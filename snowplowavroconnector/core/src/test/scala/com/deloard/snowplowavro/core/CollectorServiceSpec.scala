//package com.deloard.snowplowavro.core
//
//import java.io.File
//
//import org.scalatest.{Matchers, WordSpec}
//import com.deloard.snowplowavro.MyEventRecord
//import com.example.avro.User
//
//class CollectorServiceSpec extends WordSpec with Matchers {
//  "Sample test" should {
//    "say hello" in {
//      val stringValue = "Hello World"
//      stringValue shouldBe "Hello World"
//    }
//  }
//
//  "Get classpath" should {
//    "get the className" in {
//      val (generated, schemaMap) = Core.generate(
//        new File(
//          "/Users/adekunleba/MyProjects/dataEngineering/scala/snowplowavroconnector/core/src/main/avro/MyEventSchema.avsc"
//        )
//      )
//      generated should not be empty
//      schemaMap.keySet should not be empty
////      generated shouldBe List("Hello World")
//    }
//  }
//
//  //Using already generated class
////  "Schema path solver" should {
////    "return schema" in {
////      val sampleGenerate = User()
////      val avroProcessor =
////    }
////  }
//
//}
