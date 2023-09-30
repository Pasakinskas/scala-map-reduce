package com.pasakinskas

import com.pasakinskas.framework.{NaiveRunner, TaskRunner}
import com.pasakinskas.tasks.one.ClickCounter
import com.pasakinskas.tasks.two.LithuanianClicks
import monix.execution.Scheduler

import java.util.concurrent.Executors

object Main {

  def main(args: Array[String]): Unit = {

    lazy val numberOfThreads = 4
    lazy val executorService = scala.concurrent.ExecutionContext.fromExecutor(Executors.newFixedThreadPool(numberOfThreads));
    implicit val scheduler: Scheduler = Scheduler(executorService)

    taskOneAsync()
  }

  def taskTwo(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new LithuanianClicks, fileReader)

    naiveRunner.run().foreach(println)
  }

  def taskOne(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new ClickCounter, fileReader)

    naiveRunner.run().foreach(println)
  }

  def taskOneAsync()(implicit sc: Scheduler): Unit = {
    val taskedFileReader = new TaskedFileReader(10000)
    val taskRunner = new TaskRunner(new ClickCounter, taskedFileReader)

    taskRunner.run().runToFuture.foreach(one => {
      one.foreach(println)
    })
  }
}
