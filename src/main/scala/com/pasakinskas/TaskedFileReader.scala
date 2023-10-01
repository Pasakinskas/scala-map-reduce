package com.pasakinskas

import monix.eval.Task

import java.io.File
import java.nio.file.{FileSystems, Files}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala

class TaskedFileReader(lineLimit: Int) {

  def getEntries(path: String): Seq[Task[Seq[LineEntry]]] = {
    for {
      file <- getFiles(path)
      groupOfLines <- getFileLines(file)
    } yield groupOfLines
  }

  private def getFiles(path: String): Seq[File] = {
    val location = FileSystems.getDefault.getPath(s"./src/main/resources/${path}")
    Files.list(location).iterator().asScala
      .filter(_.toString.endsWith(".csv"))
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

//  private def getFileLines(file: File): Seq[Task[Seq[String]]] = {
//    Using(Source.fromFile(file)) { source =>
//      source.getLines().grouped(lineLimit).map(group => {
//        Task(group.toList)
//      })
//    }.getOrElse(throw new RuntimeException()).toSeq
//  }

  private def lineToEntry(headers: String, row: String): LineEntry = {
    val headerValues = headers.split(",")
    val rowValues = row.split(",")

    val valuesByHeader = rowValues.zipWithIndex.map(rowValue => {
      val (value, index) = rowValue
      headerValues(index) -> value
    }).toMap

    LineEntry(valuesByHeader)
  }
}

case class LineEntry(values: Map[String, String])
