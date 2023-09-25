package com.pasakinskas

import java.io.File
import java.nio.file.{FileSystems, Files, Path}
import scala.io.Source
import scala.jdk.CollectionConverters.IteratorHasAsScala


object FileReader extends App {

  def getFiles(path: String): Seq[Path] = {
    val location = FileSystems.getDefault.getPath(s"./src/main/resources/data/${path}")
    Files.list(location).iterator().asScala.toSeq
  }

  def getFileLines(file: File): Seq[String] = {
    Source.fromFile(file).getLines().drop(1).toSeq
  }

  def getLines(path: String) = {
    for {
      file <- getFiles(path).map(_.toFile)
      line <- getFileLines(file)
      lines = line.split(",").toSeq
    } yield lines
  }

  println(getLines("clicks"))
}
