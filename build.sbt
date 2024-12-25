ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"
libraryDependencies += "com.microsoft.onnxruntime" % "onnxruntime" % "1.15.1"

lazy val root = (project in file("."))
  .settings(
    name := "gmk-scala"
  )

