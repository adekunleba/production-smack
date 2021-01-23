package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitD

import com.akkaexamples.babsde.functionalexercise.toyzio.exhibitD.TIO.AsynchCallBack

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.util.{Failure, Success, Try}

object Runtime {

  def run[A](tio: TIO[A]): Try[A] = {
    val promise = Promise[A]()
    // tyrcomplete as a method from Try[A] => Boolean which is what AsyncCallBack is
    runAsync(tio, promise.tryComplete)
    Await.ready(promise.future, Duration.Inf)
    promise.future.value.get
  }

  def runAsync[A](tio: TIO[A], cb: AsynchCallBack[A]): Unit =
    eval(tio)(cb.asInstanceOf[AsynchCallBack[Any]])

  def eval(tio: TIO[Any])(cb: AsynchCallBack[Any]): Unit =
    tio match {
      case TIO.FlatMap(a, f: (Any => TIO[Any])) =>
        eval(a) {
          case Failure(exception) => cb(Failure(exception))
          case Success(value)     => eval(f(value))(cb)
        }
      case TIO.Fail(e) => cb(Failure(e))
      case TIO.Recover(a: TIO[Any], f) =>
        eval(a) {
          case Failure(exception) => eval(f(exception))(cb) // Remember f throwable
          case b                  => cb(b)
        }
      case TIO.Effect(a)      => cb(Try(a()))
      case TIO.EffectAsync(f) => f(cb)
    }

}
