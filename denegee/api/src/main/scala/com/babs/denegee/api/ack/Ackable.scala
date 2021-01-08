package com.babs.denegee.api.ack

import scala.util.{Failure, Success, Try}

/**
  * Base Trait for acknowledgment
  */
sealed trait Ack
case object Acknowledged extends Ack
case object NotAcknowledge extends Ack
case class AckException(message: String) extends Exception(message)

/**
  * A wrapper acknowledgement companion object
  */
object Ack {

  /**
    * If it fails in any step with parsing a strategy, it will
    * not be acknowledge
    * @param strategy
    * @return
    */
  def apply(strategy: String): Try[Ack] =
    Try({
      Option(strategy)
        .map(_.trim.toLowerCase)
        .collect({
          case "ack"          => Acknowledged
          case s if s.isEmpty => NotAcknowledge
          // Should fail if you pass anyhow acknowledgement strategy
          case _ =>
            throw AckException("Acknowledgement strategy not understood")
        })
        .getOrElse(NotAcknowledge)
    }) //.toOption.getOrElse(NotAcknowledge)
  // I think should delegate what happens with ack to client library user rather than lock in
}

trait Ackable {

  def ack: Ack

  /**
    * Default no acknowledgement
    * @return
    */
  def nAck: Ack

  /**
    * If there is an exception, it should also not acknowledge
    * @param ex
    * @return
    */
  def nAck(ex: Exception): Ack

  /**
    * Once Ack and Nack is defined you can acknowledge with a strategy
    * @param strategy - Startegy can be `ack`
    * @return
    */
  def ackWithStrategy(strategy: String): Ack =
    Ack(strategy)
      .map({
        case Acknowledged   => ack
        case NotAcknowledge => nAck
      })
      .getOrElse(nAck(AckException("Acknowledgment not recognised")))
}

object Ackable extends Ackable {
  override def ack: Ack = Acknowledged

  override def nAck: Ack = NotAcknowledge

  override def nAck(ex: Exception): Ack = NotAcknowledge
}
