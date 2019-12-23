lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "fi.orangit",
      scalaVersion := "2.12.10"
    )),
    name := "FinnPic",
    version := "0.1.0"
  )

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

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

organization := "fi.orangit"
homepage := Some(url("https://github.com/orangitfi/FinnPic"))
scmInfo := Some(ScmInfo(url("https://github.com/orangitfi/FinnPic"), "git@github.com:orangitfi/FinnBic.git"))
developers := List(Developer("vpeurala",
  "Ville Peurala",
  "ville.peurala@orangit.fi",
  url("https://github.com/vpeurala")))
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

// Add sonatype repository settings
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
