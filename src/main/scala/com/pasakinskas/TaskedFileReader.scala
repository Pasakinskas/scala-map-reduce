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
      chunkedTask = groupOfLines.map(line => linesToEntries(line))
    } yield chunkedTask
  }

  private def getFiles(path: String): Seq[File] = {
    val location = FileSystems.getDefault.getPath(s"./src/main/resources/${path}")
    Files.list(location).iterator().asScala.toSeq.map(_.toFile)
  }

  private def getFileLines(file: File): Iterator[Task[Seq[String]]]  = {
    Source.fromFile(file).getLines().grouped(lineLimit).map(group => Task(group))
  }

//  private def getFileLines(file: File): Seq[Task[Seq[String]]] = {
//    Using(Source.fromFile(file)) { source =>
//      source.getLines().grouped(lineLimit).map(group => {
//        Task(group.toList)
//      })
//    }.getOrElse(throw new RuntimeException()).toSeq
//  }

  private def linesToEntries(rawLines: Seq[String]): Seq[LineEntry] = {
    val headers = rawLines.head.split(",")
    val lineValues = rawLines.drop(1)

    lineValues.map(line => {
      val values = line.split(",").zipWithIndex.map(splitLine => {
        val (value, index) = splitLine
        headers(index) -> value
      }).toMap

      LineEntry(values)
      })
    }
}

case class LineEntry(values: Map[String, String])
