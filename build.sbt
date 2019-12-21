lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.1"
    )),
    name := "finnish-personal-identity-code"
  )

scalacOptions += "-deprecation"

libraryDependencies += "com.vladsch.flexmark" % "flexmark-all" % "0.50.44" % Test
libraryDependencies += "org.pegdown" % "pegdown" % "1.6.0" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test

coverageEnabled := true

autoAPIMappings := true

exportJars := true

scalacOptions in (doc) ++= Opts.doc.externalAPI(List
  (file(s"${(packageBin in Compile).value}") -> url("https://www.scala-lang.org/api/current"))
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/")

doctestTestFramework := DoctestTestFramework.ScalaTest

