package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitF

import java.util.concurrent.atomic.AtomicReference

import com.akkaexamples.babsde.functionalexercise.toyzio.exhibitD.TIO.AsynchCallBack

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.util.{Failure, Success, Try}

object Runtime {

  private val executor = Executor.fixed(16, "tio-default")

  def run[A](tio: TIO[A]): Try[A] = {
    val promise = Promise[A]()
    // tyrcomplete as a method from Try[A] => Boolean which is what AsyncCallBack is
    runAsync(tio, promise.tryComplete)
    Await.ready(promise.future, Duration.Inf)
    promise.future.value.get
  }

  def runAsync[A](tio: TIO[A], cb: AsynchCallBack[A]): Unit =
    new FiberRuntime(tio).onDone(cb.asInstanceOf[AsynchCallBack[Any]]).start()

  class FiberRuntime[A](tio: TIO[A]) extends Fibre[A] { self =>

    private val joined: AtomicReference[Set[AsynchCallBack[A]]] = new AtomicReference(Set.empty[AsynchCallBack[A]])
    private val result: AtomicReference[Option[Try[A]]]         = new AtomicReference[Option[Try[A]]](None)

    def onDone(done: AsynchCallBack[A]): FiberRuntime[A] = {
      joined.updateAndGet(_ + done) // add back to join
      result.get.foreach(done)
      self
    }

    def fiberDone(a: Try[Any]): Unit = {
      result.set(Some(a.asInstanceOf[Try[A]]))
      joined.get.foreach(_(a.asInstanceOf[Try[A]]))
    }

    /***
      * Once fiber starts it runs fiberDone
      * @return
      */
    def start(): FiberRuntime[A] = {
      eval(tio)(fiberDone)
      self //Returns self
    }
  }

  def eval(tio: TIO[Any])(cb: AsynchCallBack[Any]): Unit =
    executor.submit {
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
        case TIO.Effect(a)                      => cb(Try(a()))
        case TIO.EffectAsync(f)                 => f(cb)
        case TIO.Fork(tioRes)                   => cb(Success(new FiberRuntime(tioRes).start())) // on Fork start a new Fibre runtime
        case TIO.Join(fibre: FiberRuntime[Any]) => fibre.onDone(cb)
      }
    }

}
