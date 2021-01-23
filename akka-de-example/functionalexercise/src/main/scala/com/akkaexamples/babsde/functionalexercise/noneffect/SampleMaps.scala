package com.akkaexamples.babsde.functionalexercise.noneffect

import scala.collection.immutable.HashMap
import scala.collection.mutable

sealed trait Animal
case class Dog(name: String) extends Animal
case class Cat(name: String) extends Animal

object SampleMaps extends App {

  // Scala hasm
  val hashMap   = HashMap.empty[Animal, Int]
  val finalHash = hashMap.updated(Dog("bingo"), 5).updated(Cat("Mieeo"), 6).updated(Dog("bingo"), 22)

  finalHash.foreach({
    case (animal, numb) => println(s"${animal.toString} - $numb")
  })

  val mutHashMap = mutable.HashMap.empty[Animal, Int]
  mutHashMap.update(Dog("bingo"), 5)
  mutHashMap.update(Cat("Mieeo"), 6)
  mutHashMap.update(Dog("bingo"), 22)

  mutHashMap.foreach({
    case (animal, numb) => println(s"${animal.toString} - $numb")
  })

}
