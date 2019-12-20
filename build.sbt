lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.1"
    )),
    name := "finnish-personal-identity-code"
  )

scalacOptions += "-deprecation"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test

coverageEnabled := true

autoAPIMappings := true

exportJars := true

scalacOptions in (doc) ++= Opts.doc.externalAPI(List
  (file(s"${(packageBin in Compile).value}") -> url("https://www.scala-lang.org/api/current"))
)
