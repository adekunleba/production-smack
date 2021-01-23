package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitF

import scala.util.Try

trait TIO[+A] {
  self =>

  /**
    * Remember Flatmap yeah. should take an existing A and return a TIO B
    *
    * @param f - function that maps A => TIO[B]
    * @tparam B
    * @return
    */
  def flatMap[B](f: A => TIO[B]): TIO[B] = TIO.FlatMap(self, f)

  def map[B](f: A => B): TIO[B] = flatMap(a => TIO.effect(f(a)))

  def *>[B](b: => TIO[B]): TIO[B] = flatMap(_ => b)

  def recover[A1 >: A](f: Throwable => TIO[A1]): TIO[A1] = TIO.Recover(self, f)

  def fork: TIO[Fibre[A]] = TIO.Fork(self)

}

object TIO {

  type AsynchCallBack[A] = Try[A] => Unit

  /**
    * Effect of A is a simple implementation coming from type A.
    * It is a lazy evaluation `call by name`
    * It is something that is capable of working on anything to produce
    * A
    * @tparam A
    */
  def effect[A](f: => A): TIO[A] = Effect(() => f)

  def fail(e: Throwable): TIO[Nothing] = Fail(e)

  /**
    * effect that does asynchronous runs
    * @param cb - Calback
    * @tparam A
    * @return
    */
  def asynchEffect[A](cb: AsynchCallBack[A] => Unit): TIO[A] = EffectAsync(cb)

  /**
    * Implementing the effects and flatmaps
    * Because of you have case class Types of this, you can easily extract them
    * with scala match and combine a lot of processes.
    */
  case class Effect[A](a: () => A) extends TIO[A]
  case class Fail[A](e: Throwable) extends TIO[Nothing]
  case class FlatMap[A, B](a: TIO[A], f: A => TIO[B]) extends TIO[B]
  case class EffectAsync[A](f: AsynchCallBack[A] => Unit) extends TIO[A]
  case class Fork[A](tio: TIO[A]) extends TIO[Fibre[A]] // Here we are creating new Fibre
  case class Join[A](tio: Fibre[A]) extends TIO[A]      // Join returns us to TIO[A] from most likely Fibre

  /**
    * Recover is basically something that you can use to take a throwable back to some other
    * value that is a subtype of A. Hence the reason for the `f`
    */
  case class Recover[A, A1 <: A](a: TIO[A], f: Throwable => TIO[A1]) extends TIO[A1]

  def foreach[A, B](xs: Iterable[A])(f: A => TIO[B]): TIO[Iterable[B]] =
    xs.foldLeft(TIO.effect(Vector.empty[B]))((acc, curr) =>
      for {
        soFar <- acc     // Because it is TIO (flatmap is present)
        x     <- f(curr) // f: function maps A => TIO{B]
      } yield soFar :+ x
    )

  def foreachPar[A, B](xs: Iterable[A])(f: A => TIO[B]): TIO[Iterable[B]] =
    foreach(xs)(a => f(a).fork).flatMap(fibres => foreach(fibres)(_.join))
}
