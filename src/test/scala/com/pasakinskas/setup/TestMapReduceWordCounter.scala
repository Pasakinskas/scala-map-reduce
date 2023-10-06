package com.pasakinskas.setup

import com.pasakinskas.framework.{KeyValue, MapReduce, Mapper}
import com.pasakinskas.setup.TestMapReduceWordCounter._

class TestMapReduceWordCounter extends MapReduce[String, Int, (String, Int)] {
  override def mappers(): Map[String, Mapper[String, Int]] = Map {
    TEST_INPUT_FILE -> new WordCounterMapper
  }

  override def reducer(input: KeyValue[String, Seq[Int]]): Option[(String, Int)] = {
    Some((input.key, input.value.size))
  }

  override def output: String = TEST_OUTPUT_FILE

  override def outputFormat(reduceResult: (String, Int)): String = {
    s"${reduceResult._1}:${reduceResult._2}"
  }
}
object TestMapReduceWordCounter {
  final val TEST_INPUT_FILE = "test-input"
  final val TEST_OUTPUT_FILE = "test-output.csv"
}
