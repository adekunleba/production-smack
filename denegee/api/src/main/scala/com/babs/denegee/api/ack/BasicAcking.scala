package com.babs.denegee.api.ack

import cats.data.State
import com.babs.denegee.common.logging.LoggingAdapter

case class BasicAcking() extends Ackable with LoggingAdapter {

  class AckCounter
  object AckCounter {
    // This represented as var to not make the state pure any longer for every acknowledgement
    private[BasicAcking] var counter: State[Int, Unit] =
      State.pure(())
  }

  class NAckCounter
  object NAckCounter {
    // This represented as var to not make the state pure any longer for every acknowledgement
    private[BasicAcking] var counter: State[Int, Unit] =
      State.pure(())
  }

  private def ackStore: State[Int, Unit] =
    AckCounter.counter
      .modify(_ + 1)
      .map({ _ =>
        ()
      })
  private def nackStore: State[Int, Unit] =
    NAckCounter.counter
      .modify(_ + 1)
      .map({ _ =>
        ()
      })

  override def ack: Ack = {
    AckCounter.counter = ackStore
    Acknowledged
  }

  /**
    * Default no acknowledgement
    *
    * @return
    */
  override def nAck: Ack = {
    NAckCounter.counter = nackStore
    NotAcknowledge
  }

  /**
    * If there is an exception, it should also not acknowledge
    *
    * @param ex
    * @return
    */
  override def nAck(ex: Exception): Ack = {
    NAckCounter.counter = nackStore
    NotAcknowledge
  }

  def getAcknowledged: Int = AckCounter.counter.runEmpty.value._1

  def getNAcknowledged: Int = NAckCounter.counter.runEmpty.value._1
}
