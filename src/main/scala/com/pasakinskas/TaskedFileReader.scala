package com.pasakinskas

import com.pasakinskas.TaskedFileReader._
import monix.eval.Task

import java.io.File
import java.nio.file.{FileSystems, Files}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

class TaskedFileReader(lineLimit: Int) {

  def getEntries(path: String): Seq[Task[Seq[LineEntry]]] = {
    getFiles(path).flatMap(getFileLines)
  }

  private def getFiles(path: String): Seq[File] = {
    val location = FileSystems.getDefault.getPath(s"${DEFAULT_PATH}${path}")
    Files.list(location).iterator().asScala
      .filter(_.toString.endsWith(CSV_FILE_EXTENSION))
      .map(_.toFile)
      .toSeq
  }

  private def getFileLines(file: File): Iterator[Task[Seq[LineEntry]]]  = {
    val lines = Source.fromFile(file).getLines()
    val headers = lines.next()
    lines
      .filter(_.nonEmpty)
      .map(line => lineToEntry(headers, line))
      .grouped(lineLimit)
      .map(group => Task(group.toList))
  }

  private def lineToEntry(headers: String, row: String): LineEntry = {
    val headerValues = headers.split(VALUE_SEPARATOR)
    val rowValues = row.split(VALUE_SEPARATOR)

    val valuesByHeader = rowValues.zipWithIndex.map(rowValue => {
      val (value, index) = rowValue
      headerValues(index) -> value
    }).toMap

    LineEntry(valuesByHeader)
  }
}

object TaskedFileReader {
  final val DEFAULT_PATH = "./src/main/resources/"
  final val VALUE_SEPARATOR = ","
  final val CSV_FILE_EXTENSION = "csv"
}

case class LineEntry(values: Map[String, String])
