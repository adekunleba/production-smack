package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitB

trait TIOApp {

  /**
    * So somebody needs to implement the run
    * @return
    */
  def run: TIO[Any]

  def main(args: Array[String]): Unit =
    Runtime.run(run).get
}
