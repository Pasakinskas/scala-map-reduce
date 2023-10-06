ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val monixVersion = "3.4.1"
val specs2Version = "4.19.2"
val scalamockVersion = "5.2.0"

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % monixVersion,
  "org.specs2" %% "specs2-core" % specs2Version % Test,
  "org.scalamock" %% "scalamock" % scalamockVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "scala-map-reduce"
  )