ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(name := "project-challenge")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.scalatestplus" %% "scalacheck-1-17" % "3.2.14.0" % Test,
  ("com.storm-enroute" %% "scalameter" % "0.21" % Test).cross(CrossVersion.for3Use2_13).exclude("org.scala-lang.modules", "scala-xml_2.13")
)
