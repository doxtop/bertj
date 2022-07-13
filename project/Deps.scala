import sbt._

object Deps {
    lazy val fj      = "org.functionaljava" % "functionaljava" % "4.9"
    lazy val junitIn = "com.novocode" % "junit-interface" % "0.11"
    lazy val junit   = "junit" % "junit" % "4.12"
    lazy val tests = Seq(junit, junitIn).map(_ % Test)
}
