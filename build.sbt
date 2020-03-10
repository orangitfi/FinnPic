resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

import sbt.Keys.{libraryDependencies, organization}

val scala_2_13 = "2.13.1"
val scala_2_12 = "2.12.10"
val scala_2_11 = "2.11.12"
val scala_2_10 = "2.10.7"
val supportedScalaVersions = Seq(scala_2_10, scala_2_11, scala_2_12, scala_2_13)

ThisBuild / scalaVersion := scala_2_12

lazy val root = (project in file(".")).aggregate(finnpicJS, finnpicJVM)

lazy val finnpic = crossProject
  .in(file("."))
  .settings(
    name := "finnpic",
    organization := "org.finnpic",
    version := "0.4.2",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.3" % Test,
    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.1.0" % Test,
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test,
    // Publishing information
    homepage := Some(url("https://finnpic.org")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/orangitfi/FinnPic"),
        "git@github.com:orangitfi/FinnPic.git"
      )
    ),
    developers := List(
      Developer(
        "vpeurala",
        "Ville Peurala",
        "ville.peurala@orangit.fi",
        url("https://github.com/vpeurala")
      ),
      Developer(
        "uberpallo",
        "Kasper Jaakkola",
        "kasper.jaakkola@orangit.fi",
        url("https://github.com/uberpallo")
      )
    ),
    licenses += ("Apache-2.0", url(
      "http://www.apache.org/licenses/LICENSE-2.0"
    )),
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value
      .withOverwrite(true)
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.6"
  )
  .jvmSettings(
    // TODO vpeurala: Enable this when sbt-doctest supports Scala.js.
    // doctestTestFramework := DoctestTestFramework.ScalaTest
    libraryDependencies += "junit" % "junit" % "4.13" % Test,
    javacOptions += "-Xdiags:verbose"
  )

lazy val finnpicJS = finnpic.js
lazy val finnpicJVM = finnpic.jvm

// Note: Remember to have this set to false when publishing and true otherwise. - vpeurala, 30.12.2019
coverageEnabled := true

exportJars := true

autoAPIMappings := true
scalacOptions in (doc) ++= Opts.doc.externalAPI(
  List(
    file(s"${(packageBin in Compile).value}") -> url(
      "https://www.scala-lang.org/api/current"
    )
  )
)

// Note: This does not work with ScalaTest 3.1.0: java.lang.NoClassDefFoundError: com/vladsch/flexmark/ast/Node
// - vpeurala, 23.12.2019
// testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports/")
skip in publish := true
