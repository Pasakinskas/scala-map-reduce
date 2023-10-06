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

    taskOne()
    taskTwo()
  }

  def taskOne()(implicit sc: Scheduler): Unit = {
    val fileReader = new FileReaderWriter()
    val taskRunner = new TaskRunner(new UsersClicksDataJoin, fileReader)

    taskRunner.outputResult().runToFuture
  }

  def taskTwo()(implicit sc: Scheduler): Unit = {
    val fileReader = new FileReaderWriter()
    val taskRunner = new TaskRunner(new ClickCounter, fileReader)

    taskRunner.outputResult().runToFuture
  }
}
