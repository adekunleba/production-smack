package com.akkaexamples.babsde.akkacore

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

/**
  * Top level app runner creates the actor system and all.
  */
sealed trait RoomCommand
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
final case class SessionDenied(reason: String)                      extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

sealed trait SessionCommand
final case class PostMessage(message: String)               extends SessionCommand
final case class NotifyClient(messagePosted: MessagePosted) extends SessionCommand

object ChatRoom {

  final case class PublishMessage(screenName: String, message: String) extends RoomCommand

  /**
    * Chatroom actor has a behavior of type RoomCommand
    *
    * @return
    */
  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] = {
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, client) => {
          val sess = context.spawn(
            session(context.self, screenName, client),
            URLEncoder.encode(screenName, StandardCharsets.UTF_8.name)
          )
          client.tell(SessionGranted(sess))
          // Add sess to list of Sessions
          chatRoom(sess :: sessions)
        }
        case PublishMessage(screenName, publishMessage) => {
          context.log.info(s"Currently publishing message for $screenName")
          // Build a Notify client data and tell SessionCommand that you have posted message
          val notifyClient = NotifyClient(MessagePosted(screenName, publishMessage))
          sessions.foreach(_.tell(notifyClient))
          Behaviors.same
        }
      }
    }
  }

  private def session(
      room: ActorRef[PublishMessage],
      screenName: String,
      client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand] = {
    Behaviors.receiveMessage {
      case PostMessage(inMessage) =>
        room.tell(PublishMessage(screenName, inMessage))
        Behaviors.same
      case NotifyClient(messagePosted) =>
        client.tell(messagePosted)
        Behaviors.same
    }
  }
}

object Gabller {

  def apply(): Behavior[SessionEvent] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionGranted(handle) =>
          context.log.info("Session granted")
          handle.tell(PostMessage("Hello world"))
          Behaviors.same
        case SessionDenied(reason) =>
          context.log.error(s"Session denied for $reason")
          Behaviors.same
        case MessagePosted(screenName, postedMessage) =>
          context.log.info(s"message has been posted by $screenName: $postedMessage")
          Behaviors.stopped
      }
    }
  }
}

object ChatMain {
  def apply(): Behavior[NotUsed] = {
    Behaviors.setup { context =>
      val chatRoom   = context.spawn(ChatRoom(), "chatRoom")
      val gabblerRef = context.spawn(Gabller(), "gabller")
      context.watch(gabblerRef)
      chatRoom ! GetSession("ol’ Gabbler", gabblerRef)

      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }
  }
}
object AkkaTypedMoreComplex extends App {

  // It is of type NotUsed hence not very useful
  val system: ActorSystem[NotUsed] = ActorSystem(ChatMain(), "chatMain")
}
