ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"
libraryDependencies += "com.microsoft.onnxruntime" % "onnxruntime" % "1.15.1"

lazy val root = (project in file("."))
  .settings(
    name := "gmk-scala"
  )

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.1.0"
libraryDependencies ++= Seq(
    "com.google.inject" % "guice" % "7.0.0",
    "com.typesafe.play" %% "play-guice" % "2.9.3",  // or whatever Play version you're using
    "com.typesafe.play" %% "play-json" % "2.10.5"
)
libraryDependencies += "jakarta.inject" % "jakarta.inject-api" % "2.0.1"