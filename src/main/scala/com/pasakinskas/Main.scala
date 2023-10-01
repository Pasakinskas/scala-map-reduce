package com.pasakinskas

import com.pasakinskas.examples.one.ClickCounter
import com.pasakinskas.framework.{NaiveRunner, TaskRunner}
import com.pasakinskas.examples.two.UsersClicksDataJoin
import monix.execution.Scheduler

import java.util.concurrent.Executors

object Main {

  def main(args: Array[String]): Unit = {

    lazy val numberOfThreads = 4
    lazy val executorService = scala.concurrent.ExecutionContext.fromExecutor(Executors.newFixedThreadPool(numberOfThreads));
    implicit val scheduler: Scheduler = Scheduler(executorService)

   taskOneAsync()
//    taskOne()
  }

  def taskTwo(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new UsersClicksDataJoin, fileReader)

    naiveRunner.run().foreach(println)
  }

  def taskOne(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new ClickCounter, fileReader)

    val t0 = System.currentTimeMillis()
    naiveRunner.run().foreach(println)
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
  }

  def taskOneAsync()(implicit sc: Scheduler): Unit = {
    val taskedFileReader = new TaskedFileReader(10000)
    val taskRunner = new TaskRunner(new ClickCounter, taskedFileReader)

    val t0 = System.currentTimeMillis()
    taskRunner.run().runToFuture.andThen(_ => {
      val t1 = System.currentTimeMillis()
      println("Elapsed time: " + (t1 - t0) + "ms")
    })
  }
}
