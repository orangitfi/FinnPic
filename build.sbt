import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

val scala_2_13 = "2.13.1"
val scala_2_12 = "2.12.10"
val scala_2_11 = "2.11.12"
val scala_2_10 = "2.10.7"
val scala_js = "sjs0.6.31"

val supportedScalaVersionsOnJvm = List(scala_2_10, scala_2_11, scala_2_12, scala_2_13)

lazy val root = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(
    inThisBuild(List(
      scalaVersion := scala_2_12
    )),
    name := "FinnPic",
    organization := "fi.orangit",
    version := "0.1.0-SNAPSHOT",
  )
  .jsSettings(
    // Note: Doctest does not work on Scala.js as of 23.12.2019. - vpeurala
    doctestIgnoreRegex := Some(".*"),
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.6"
  )
  .jvmSettings(
    doctestTestFramework := DoctestTestFramework.ScalaTest,
    crossScalaVersions := supportedScalaVersionsOnJvm
  )

enablePlugins(ScalaJSPlugin)

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.vladsch.flexmark" % "flexmark-all" % "0.50.44" % Test
libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test
libraryDependencies += "org.scalactic" %%% "scalactic" % "3.1.0" % Test
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test

coverageEnabled := true

autoAPIMappings := true

exportJars := true

scalacOptions in (doc) ++= Opts.doc.externalAPI(List
(file(s"${(packageBin in Compile).value}") -> url("https://www.scala-lang.org/api/current"))
)

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/")

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
