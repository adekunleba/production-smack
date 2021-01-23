package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitC

import scala.concurrent.Promise
import scala.util.{Failure, Success, Try}

object Runtime {

  /**
    * Runtime is more like the processor of a TIO
    *
    * @param tio
    * @tparam A
    * @return
    */
  def run[A](tio: TIO[A]): Try[A] = tio match {
    case TIO.FlatMap(a, f: (Any => TIO[A])) => run(a).flatMap(v => run(f(v)))
    case TIO.Fail(e) => Failure(e)
    case TIO.Recover(a: TIO[A], f) =>
      run(a) match {
        case Failure(exception) => run(f(exception)) // Remember f throwable
        case Success(value)     => Success(value)
      }
    case TIO.Effect(a) => Try(a())
  }

}
