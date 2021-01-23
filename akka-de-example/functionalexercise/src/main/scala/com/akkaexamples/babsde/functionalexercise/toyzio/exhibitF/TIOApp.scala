package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitF

import java.util.{Timer, TimerTask}

import scala.concurrent.duration.Duration
import scala.util.Success

trait TIOApp {

  /**
    * So somebody needs to implement the run
    *
    * @return
    */
  def run: TIO[Any]

  def main(args: Array[String]): Unit =
    Runtime.run(run).get

  val timer = new Timer(true)

  /**
    * The sleep here is just as simple as you are calling
    * TIO.asyncEffect in place of TIO.effect to basically
    * run the stuff in a concurrent approach.
    * @param d
    * @return
    */
  def simpleSleep(d: Duration): TIO[Unit] =
    TIO.asynchEffect[Unit] { cb =>
      timer.schedule(new TimerTask {
        override def run(): Unit = cb(Success(()))
      }, d.toMillis)
    }
}
