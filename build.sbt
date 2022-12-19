ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

val PerformanceTest = config("performance") extend Test

lazy val performanceTestSettings =
  inConfig(PerformanceTest)(Defaults.testSettings) ++
    Seq(
      PerformanceTest / fork              := false,
      PerformanceTest / parallelExecution := false,
      PerformanceTest / scalaSource       := baseDirectory.value / "src/performance/scala",
      PerformanceTest / logBuffered       := false,
      PerformanceTest / testOptions := Seq(
//        Tests.Filter((_: String).startsWith("com.intellias.challenge.performance.")),
        Tests.Argument("-oD")
      )
    )

lazy val root = (project in file("."))
  .configs(PerformanceTest)
  .settings(
    name := "project-challenge",
    libraryDependencies ++= Seq(
      "org.scalatest"      %% "scalatest"       % "3.2.14"   % Test,
      "org.scalatestplus"  %% "scalacheck-1-17" % "3.2.14.0" % Test,
      ("com.storm-enroute" %% "scalameter"      % "0.21"     % Test)
        .cross(CrossVersion.for3Use2_13)
        .exclude("org.scala-lang.modules", "scala-xml_2.13")
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    performanceTestSettings,
  )
