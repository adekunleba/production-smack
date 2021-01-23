package com.akkaexamples.babsde.functionalexercise.toyzio.exhibitE

import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

trait Executor {
  final def submit(thunk: => Unit): Unit = submitRunnable(() => thunk)
  def submitRunnable(thunk: Runnable): Unit
}

object Executor {
  private val threadCounter = new AtomicInteger()
  private def nexThreadId   = threadCounter.incrementAndGet()

  def fixed(threads: Int, namedPrefix: String): Executor = {
    val executor = newFixedThreadPool(threads, namedDeamondThreads(namedPrefix))
    thunk => executor.submit(thunk)
  }

  private def namedDeamondThreads(namePrefix: String): ThreadFactory = { thunk =>
    val thread: Thread = new Thread(thunk, s"$namePrefix-$nexThreadId")
    thread.setDaemon(true)
    thread.setUncaughtExceptionHandler((_, e) => e.printStackTrace())
    thread
  }
}
