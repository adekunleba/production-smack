package com.akkaexamples.babsde.akkastream.binanceWebSocketStream.actorProps

import akka.Done
import akka.actor.Actor
import com.akkaexamples.babsde.akkastream.binanceWebSocketStream.actorProps.ConvertToInt.{
  ConvertValue,
  Failure,
  Success
}

import scala.util.Try

object ConvertToInt {
  case class ConvertValue(value: String)
  case object Failure
  case object Success
}

class ConvertToInt extends Actor {
  override def receive: Receive = {
    // TODO: Should put a guard in case of failure
    case ConvertValue(value) =>
      Try(value.toFloat) match {
        case scala.util.Success(valueConvert) =>
          println(s"Convert value converted value $value to float $valueConvert")
          sender() ! Success
        case scala.util.Failure(_) => sender() ! Failure
      }

    // TODO: Find a better way to manage this graph
    case Success =>
      println("Succesfully converted data to float")
    // sender() ! Done This may have caused dead letters cos it kills the actor
    case Failure =>
      // Manage failure
      println("Error converting data, sending the stream to DB as is")
    // sender() ! Done
  }
}
