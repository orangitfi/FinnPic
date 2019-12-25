addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "3.0.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
// Note: Doctests disabled for now, because they are not supported on Scala.js.
// - vpeurala, 25.12.2019
// addSbtPlugin("com.github.tkawachi" % "sbt-doctest" % "0.9.5")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.31")
addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.7")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
