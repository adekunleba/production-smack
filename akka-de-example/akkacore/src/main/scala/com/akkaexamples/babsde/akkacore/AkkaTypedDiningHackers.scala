package com.akkaexamples.babsde.akkacore

import akka.NotUsed
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.akkaexamples.babsde.akkacore.ChopStick.{ChopStickAnswer, ChopstickMessage}
import com.akkaexamples.babsde.akkacore.Hakker.{HandleChopStickAnswer, Think}

import scala.concurrent.duration._

object ChopStick {
  sealed trait ChopstickMessage
  final case class Take(ref: ActorRef[ChopStickAnswer]) extends ChopstickMessage
  final case class Put(ref: ActorRef[ChopStickAnswer])  extends ChopstickMessage

  sealed trait ChopStickAnswer
  final case class Taken(chopstickMessage: ActorRef[ChopstickMessage]) extends ChopStickAnswer
  final case class Busy(chopstickMessage: ActorRef[ChopstickMessage])  extends ChopStickAnswer

  def apply(): Behavior[ChopstickMessage] = available()

  def takenBy(hakker: ActorRef[ChopStickAnswer]): Behavior[ChopstickMessage] = {
    Behaviors.receive {
      case (ctx, Take(otherHakker)) =>
        otherHakker ! Busy(ctx.self)
        Behaviors.same

      case (_, Put(`hakker`)) => available()
      case _                  => Behaviors.unhandled
    }
  }

  def available(): Behavior[ChopstickMessage] = {
    Behaviors.receivePartial {
      case (ctx, Take(hakker)) =>
        hakker ! Taken(ctx.self)
        takenBy(hakker)
    }
  }
}

object Hakker {

  sealed trait Command
  case object Think                                            extends Command
  case object Eat                                              extends Command
  final case class HandleChopStickAnswer(msg: ChopStickAnswer) extends Command

  def apply(name: String, left: ActorRef[ChopstickMessage], right: ActorRef[ChopstickMessage]): Behavior[Command] =
    Behaviors.setup { ctx =>
      new Hakker(ctx, name, left, right).waiting
    }
}

/**
  * This is guardian actor
  */
object DiningHakkers {
  def apply(): Behavior[NotUsed] = Behaviors.setup { context =>
    //Spine 5 Chopstick ActorRef
    val chopsticks = for (i <- 1 to 5) yield context.spawn(ChopStick(), "Chopstick" + i)
    val hakkers = for {
      (name, i) <- List("Ghosh", "Boner", "Klang", "Krasser", "Manie").zipWithIndex
    } yield context.spawn(Hakker(name, chopsticks(i), chopsticks((i + 1) % 5)), name)
    hakkers.foreach(_ ! Hakker.Think)
    Behaviors.empty

  }
}

class Hakker(
    ctx: ActorContext[Hakker.Command],
    name: String,
    left: ActorRef[ChopstickMessage],
    right: ActorRef[ChopstickMessage]
) {
  val adapter: ActorRef[ChopStickAnswer] = ctx.messageAdapter(HandleChopStickAnswer)

  val eating: Behavior[Hakker.Command] = {
    Behaviors.receiveMessagePartial({
      case Think =>
        ctx.log.info(s"$name puts down his chopstick and starts to think")
        left ! ChopStick.Put(adapter)
        right ! ChopStick.Put(adapter)
        startThinking(ctx, 5.seconds)
    })
  }

  def startEating(ctx: ActorContext[Hakker.Command], seconds: FiniteDuration): Behavior[Hakker.Command] = {
    Behaviors.withTimers({ timers =>
      timers.startSingleTimer(Hakker.Eat, Hakker.Eat, seconds)
      eating
    })
  }

  def waitForOtherChopStick(
      chopstickToWaitFor: ActorRef[ChopstickMessage],
      takenChopStick: ActorRef[ChopstickMessage]
  ): Behavior[Hakker.Command] = Behaviors.receiveMessagePartial {
    case HandleChopStickAnswer(ChopStick.Taken(`chopstickToWaitFor`)) =>
      ctx.log.info(s"$name has picked up ${left.path.name} and ${right.path.name} and starts to eat")
      startEating(ctx, 5.seconds)
    case HandleChopStickAnswer(ChopStick.Busy(`chopstickToWaitFor`)) =>
      takenChopStick ! ChopStick.Put(adapter)
      startThinking(ctx, 10.milliseconds)
  }

  val firstChopStickDenied: Behavior[Hakker.Command] = {
    Behaviors.receiveMessagePartial {
      case HandleChopStickAnswer(ChopStick.Taken(chopstick)) =>
        chopstick ! ChopStick.Put(adapter)
        startThinking(ctx, 10.milliseconds)

      case HandleChopStickAnswer(ChopStick.Busy(_)) =>
        startThinking(ctx, 10.milliseconds)
    }
  }

  val hungry: Behavior[Hakker.Command] = Behaviors.receiveMessage {
    case HandleChopStickAnswer(ChopStick.Taken(`left`)) =>
      waitForOtherChopStick(chopstickToWaitFor = right, takenChopStick = left)

    case HandleChopStickAnswer(ChopStick.Taken(`right`)) =>
      waitForOtherChopStick(chopstickToWaitFor = left, takenChopStick = right)

    case HandleChopStickAnswer(ChopStick.Busy(_)) => firstChopStickDenied
  }

  def startThinking(ctx: ActorContext[Hakker.Command], seconds: FiniteDuration): Behavior[Hakker.Command] =
    Behaviors.withTimers[Hakker.Command] { timers =>
      timers.startSingleTimer(Hakker.Eat, Hakker.Eat, seconds)
      thinking
    }

  val waiting: Behavior[Hakker.Command] = Behaviors.receiveMessagePartial {
    case Think =>
      ctx.log.info("{} starts to think", name)
      startThinking(ctx, 5.seconds)
  }

  val thinking: Behavior[Hakker.Command] = Behaviors.receiveMessagePartial {
    case Think =>
      left ! ChopStick.Take(adapter)
      right ! ChopStick.Take(adapter)
      hungry
  }

}

object AkkaTypedDiningHackers extends App {
  ActorSystem(DiningHakkers(), "DiningHakkers")
}
