package com.pasakinskas.framework

import com.pasakinskas.helpers.FileReaderWriterHelper
import com.pasakinskas.setup.TestMapReduceWordCounter
import monix.execution.schedulers.TestScheduler
import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration.Duration
import scala.concurrent.Await

class TaskRunnerTest extends Specification {

  "TaskRunnerTest run" should {
    "return a reduce result" in new Context {
      val futureResult = taskRunner.run().runToFuture(sc)
      sc.tick()

      val actual = Await.result(futureResult, Duration(1, "s"))
      actual mustEqual reduceResult
    }
  }

  "TaskRunnerTest outputResult" should {
    "formats and saves reduce result to a file" in new Context {
      (fileReader.writeToFile _).expects(outputLines, mapReduce.output).once()

      taskRunner.outputResult().runToFuture(sc)
      sc.tick()
    }
  }

  class Context extends Scope with MockContext {

    implicit val sc = TestScheduler()

    val inputData = Seq(
      Map("word" -> "test"),
      Map("word" -> "this"),
      Map("word" -> "is"),
      Map("word" -> "a"),
      Map("word" -> "test"),
    )
    val reduceResult = Seq(("test", 2), ("this", 1), ("is", 1), ("a", 1))
    val outputLines = Seq("test2", "this:1", "is:1", "a:1")

    val mapReduce = new TestMapReduceWordCounter

    val fileReader = mock[FileReaderWriter]
    val input = FileReaderWriterHelper.entriesFromList(inputData)
    (fileReader.getEntries _).expects(TestMapReduceWordCounter.TEST_INPUT_FILE).returning(input)

    val taskRunner = new TaskRunner(mapReduce, fileReader)
  }
}
