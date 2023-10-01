package com.pasakinskas.framework

import com.pasakinskas.framework.FileReader._
import monix.eval.Task

import java.io.File
import java.nio.file.{FileSystems, Files}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

class FileReader(lineLimit: Int) {

  def getEntries(path: String): Seq[Task[Seq[LineEntry]]] = {
    for {
      file <- getFiles(path)
      fileContents = getFileLines(file)
      chunkOfLines <- fileContents.lines
      parsedChunks = chunkOfLines.map(lines => {
        lines.map(line => lineToEntry(fileContents.headers, line))
      })
    } yield parsedChunks
  }

  private def getFiles(path: String): Seq[File] = {
    val location = FileSystems.getDefault.getPath(s"${DEFAULT_PATH}${path}")

    Files.list(location).iterator().asScala
      .filter(_.toString.endsWith(CSV_FILE_EXTENSION))
      .map(_.toFile)
      .toSeq
  }

  private def getFileLines(file: File): FileContents  = {
    val lines = Source.fromFile(file).getLines()
    val headers = lines.next()
    val res = lines.filter(_.nonEmpty).grouped(lineLimit).map(Task(_))

    FileContents(headers, res)
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

object FileReader {
  final val DEFAULT_PATH = "./src/main/resources/"
  final val VALUE_SEPARATOR = ","
  final val CSV_FILE_EXTENSION = "csv"
}

case class FileContents(headers: String, lines: Iterator[Task[Seq[String]]])

case class LineEntry(values: Map[String, String])
