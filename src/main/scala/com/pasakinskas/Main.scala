package com.pasakinskas

import com.pasakinskas.examples.one.ClickCounter
import com.pasakinskas.framework.{FileReaderWriter, TaskRunner}
import com.pasakinskas.examples.two.UsersClicksDataJoin
import monix.execution.Scheduler

import java.util.concurrent.Executors

object Main {

  def main(args: Array[String]): Unit = {
    val size = 4
    val executorService = scala.concurrent.ExecutionContext.fromExecutor(Executors.newFixedThreadPool(size))
    implicit val scheduler: Scheduler = Scheduler(executorService)

    task()
  }

  def task()(implicit sc: Scheduler): Unit = {
    val lineLimit = 10000
    val fileReader = new FileReaderWriter(lineLimit)
    val taskRunner = new TaskRunner(new UsersClicksDataJoin, fileReader)

    val t0 = System.currentTimeMillis()
    taskRunner.run().runToFuture.andThen(_ => {
      val t1 = System.currentTimeMillis()
      println("Elapsed time: " + (t1 - t0) + "ms")
    })
  }
}
