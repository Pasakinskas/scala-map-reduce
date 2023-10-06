package com.pasakinskas.helpers

import com.pasakinskas.framework.LineEntry
import monix.eval.Task

object FileReaderWriterHelper {
  def entriesFromList(input: Seq[Map[String, String]]): Seq[Task[Seq[LineEntry]]] = {
    Seq(Task(input.map(line => LineEntry(line))))
  }
}
