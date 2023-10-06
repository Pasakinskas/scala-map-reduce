package com.pasakinskas.framework

import com.pasakinskas.framework.FileReaderWriter._
import monix.eval.Task

import java.io.{File, PrintWriter}
import java.nio.file.{FileSystems, Files}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

class FileReaderWriter(lineLimit: Int = 10000) {

  def getEntries(path: String): Seq[Task[Seq[LineEntry]]] = {
    for {
      file <- getPathFiles(path)
      fileContents = readFile(file)
      chunks <- fileContents.lines
      parsedChunks = chunks.map(lines => {
        lines.map(line => lineToEntry(fileContents.headers, line))
      })
    } yield parsedChunks
  }

  def writeToFile(lines: Seq[String], fileName: String): Unit = {
      val writer = new PrintWriter(new File(s"$DEFAULT_PATH/$fileName"))
      lines.foreach(line => writer.write(line + "\n"))
      writer.flush()
      writer.close()
  }

  private def getPathFiles(path: String): Seq[File] = {
    val location = FileSystems.getDefault.getPath(s"${DEFAULT_PATH}${path}")

    Files.list(location).iterator().asScala
      .filter(_.toString.endsWith(CSV_FILE_EXTENSION))
      .map(_.toFile)
      .toSeq
  }

  private def readFile(file: File): FileContents  = {
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

object FileReaderWriter {
  final val DEFAULT_PATH = "./src/main/resources/"
  final val VALUE_SEPARATOR = ","
  final val CSV_FILE_EXTENSION = "csv"
}

case class FileContents(headers: String, lines: Iterator[Task[Seq[String]]])

case class LineEntry(values: Map[String, String])
