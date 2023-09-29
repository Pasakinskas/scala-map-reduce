package com.pasakinskas

import java.io.File
import java.nio.file.{FileSystems, Files, Path}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala


class FileReader {

  // investigate using an future/observable here later
  def getEntries(path: String): Seq[Seq[Map[String, String]]] = {
    for {
      file <- getFiles(path).map(_.toFile)
      entry = linesToEntries(getFileLines(file))
    } yield entry
  }

  private def getFiles(path: String): Seq[Path] = {
    val location = FileSystems.getDefault.getPath(s"./src/main/resources/${path}")
    Files.list(location).iterator().asScala.toSeq
  }

  // source is not closed
  private def getFileLines(file: File): Seq[String] = {
    Source.fromFile(file).getLines().toSeq
  }

  // for comprehension
  private def linesToEntries(rawLines: Seq[String]): Seq[Map[String, String]] = {
    val headers = rawLines.head.split(",")
    val values = rawLines.drop(1)

    values.map(line => {
      line.split(",").zipWithIndex.map({
        case (value, index) => headers(index) -> value
      }).toMap
    })
  }
}
