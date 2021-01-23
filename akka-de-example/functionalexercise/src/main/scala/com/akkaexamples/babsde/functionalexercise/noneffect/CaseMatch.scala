package com.akkaexamples.babsde.functionalexercise.noneffect

object CaseMatch extends App {
  val isDefined = true
  (1, 2) match {
    case (a, b)              => println(s"printing 1 and 2")
    case (a, _) if isDefined => println(s"case class proceeded to check isDefined")
    case _                   => println("Whatever")
  }
}
