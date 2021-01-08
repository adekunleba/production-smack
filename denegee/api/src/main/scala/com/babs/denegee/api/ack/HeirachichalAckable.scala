package com.babs.denegee.api.ack

import com.babs.denegee.api.utils.Closeable
import com.babs.denegee.common.logging.LoggingAdapter

sealed trait CloseableState { val state: Boolean }
case object Closed extends CloseableState { val state = true }
case object UnClosed extends CloseableState { val state = false }

private[ack] case class HeirachichalAckable(
    private val parentHacks: Seq[Ackable])
    extends Closeable
    with LoggingAdapter {

  private val ackLogger = logger

  private var ackableState: CloseableState = UnClosed
  private var allChildren: Seq[ChildAckable] = Seq.empty

  def createChildAck(): ChildAckable =
    ackableState match {
      case x if x.state =>
        throw AckException(
          s"Cannot create child ack when ${getClass.getSimpleName} is closed")
      case _ =>
        val newChild = ChildAckable(allChildren.length)

        /**Should register the child ack with HeirachicalAckable**/
        allChildren = allChildren :+ newChild
        newChild
    }

  def sendAck(): Seq[Ack] =
    allChildren match {
      case x if x.map(_.ackno).reduce(_ && _) && ackableState == Closed =>
        ackLogger.info(
          s"Sending acknowledge, acknowledge ${allChildren.size} children")
        parentHacks.map(x => x.ack)
      case x if x.map(_.nacnko).reduce(_ && _) && ackableState == Closed =>
        ackLogger.info(s"${allChildren.size} children not acknowledged")
        val ex = AckException("fk acknowledge")
        parentHacks.map(x => x.nAck(ex))
      case _ =>
        Seq.empty[Ack]

    }

  override def close(): Unit = {
    ackableState = Closed
    sendAck()
    ()
  }

  case class ChildAckable(
      private[HeirachichalAckable] val childId: Int,
      private[HeirachichalAckable] val ackno: Boolean = false,
      private[HeirachichalAckable] val nacnko: Boolean = false
  ) extends Ackable {

    override def ack: Ack = {
      allChildren = allChildren.filterNot(_.childId == childId) :+ ChildAckable(
        childId,
        ackno = true
      )
      sendAck()
      Acknowledged
    }

    /**
      * Default no acknowledgement
      */
    override def nAck: Ack = {
      allChildren = allChildren.filterNot(_.childId == childId) :+ ChildAckable(
        childId,
        nacnko = true
      )
      sendAck()
      NotAcknowledge
    }

    /**
      * If there is an exception, it should also not acknowledge
      */
    override def nAck(ex: Exception): Ack = {
      sendAck()
      NotAcknowledge
    }
  }

  def parents: Seq[Ackable] = parentHacks

}

object HeirachichalAckable {
  def apply(parentHacks: Seq[Ackable]): HeirachichalAckable =
    new HeirachichalAckable(parentHacks)
}
