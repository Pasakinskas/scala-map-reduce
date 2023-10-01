package com.pasakinskas

import com.pasakinskas.examples.one.ClickCounter
import com.pasakinskas.framework.{FileReader, TaskRunner}
import com.pasakinskas.examples.two.UsersClicksDataJoin
import monix.execution.Scheduler

import java.util.concurrent.Executors

object Main {

  def main(args: Array[String]): Unit = {

    lazy val numberOfThreads = 4
    lazy val executorService = scala.concurrent.ExecutionContext.fromExecutor(Executors.newFixedThreadPool(numberOfThreads));
    implicit val scheduler: Scheduler = Scheduler(executorService)

    taskOneAsync()
  }

  def taskOneAsync()(implicit sc: Scheduler): Unit = {
    val lineLimit = 10000
    val fileReader = new FileReader(lineLimit)
    val taskRunner = new TaskRunner(new ClickCounter, fileReader)

    val t0 = System.currentTimeMillis()
    taskRunner.run().runToFuture.andThen(a => {
      a.get.foreach(println)
      val t1 = System.currentTimeMillis()
      println("Elapsed time: " + (t1 - t0) + "ms")
    })
  }
}
