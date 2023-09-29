package com.pasakinskas

import com.pasakinskas.framework.NaiveRunner
import com.pasakinskas.tasks.one.ClickCounter
import com.pasakinskas.tasks.two.LithuanianClicks

object Main {

  def main(args: Array[String]): Unit = {
    taskOne()
  }

  def taskTwo(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new LithuanianClicks, fileReader)
    naiveRunner.run()
  }

  def taskOne(): Unit = {
    val fileReader = new FileReader
    val naiveRunner = new NaiveRunner(new ClickCounter, fileReader)
    naiveRunner.run()
  }
}
