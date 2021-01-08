package com.babs.denegee.api.ack

import cats.data.State
import com.babs.denegee.common.logging.LoggingAdapter

case class BasicAckable() extends Ackable with LoggingAdapter {

  object AckCounter {
    // This represented as var to not make the state pure any longer for every acknowledgement
    private[BasicAckable] var counter: State[Int, Unit] =
      State.pure(())
  }

  object NAckCounter {
    // This represented as var to not make the state pure any longer for every acknowledgement
    private[BasicAckable] var counter: State[Int, Unit] =
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

  override def getAcknowledged: Int = AckCounter.counter.runEmpty.value._1

  override def getNAcknowledged: Int = NAckCounter.counter.runEmpty.value._1
}
