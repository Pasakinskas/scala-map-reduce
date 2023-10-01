package com.pasakinskas

import java.io.File
import java.nio.file.{FileSystems, Files, Path}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.Using


class FileReader {

  def getEntries(path: String): Seq[Seq[Map[String, String]]] = {
    for {
      file <- getFiles(path).map(_.toFile)
      entry = linesToEntries(getFileLines(file))
    } yield entry
  }

  private def getFiles(path: String): Seq[Path] = {
    val location = FileSystems.getDefault.getPath(s"./src/main/resources/${path}")
    Files.list(location).iterator().asScala.filter(_.toString.endsWith(".csv")).toSeq
  }

  private def getFileLines(file: File): Seq[String] = {
    Using(Source.fromFile(file)) { _.getLines().toList }
      .getOrElse(throw new RuntimeException("could not read line files"))
  }

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
