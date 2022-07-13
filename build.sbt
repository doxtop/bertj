val w = { sys.props += "packaging.type" -> "jar"}

import Deps._

lazy val root = (project in file("."))
    .settings(
        name := "bertj"
      , organization := "synrc"
      , version := "1.0.0"
      , description := "bert codec"
      , javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation", "-Xdiags:verbose")
      , testOptions += Tests.Argument(TestFrameworks.JUnit, "+q", "-v")
      , autoScalaLibrary := false
      , crossPaths := false
      , Test / fork := false
      , Test / connectInput := true
      , publish / skip := true
      , libraryDependencies ++= Deps.tests
      , libraryDependencies ++= Seq(Deps.fj)
    )
