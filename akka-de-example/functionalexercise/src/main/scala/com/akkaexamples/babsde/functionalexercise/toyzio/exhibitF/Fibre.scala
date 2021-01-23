package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitF

trait Fibre[+A] { self =>

  def join: TIO[A] = TIO.Join(self)
}
