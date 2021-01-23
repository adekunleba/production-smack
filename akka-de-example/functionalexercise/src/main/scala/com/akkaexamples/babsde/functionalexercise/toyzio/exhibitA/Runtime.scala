package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitA

object Runtime {

  /**
    * Runtime is more like the processor of a TIO
    *
    * @param tio
    * @tparam A
    * @return
    */
  def run[A](tio: TIO[A]): A = tio match {
    case TIO.FlatMap(a, f) => run(f(run(a)))
    case TIO.Effect(a)     => a()
  }
}
