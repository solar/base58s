name := "base58s"

organization := "org.sazabi"

version := "0.0.1-SNAPSHOT"

crossScalaVersions := Seq("2.11.7", "2.10.5")

scalaVersion := crossScalaVersions.value.head

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "com.github.scalaprops" %% "scalaprops" % "0.1.11" % "test")

incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xlint",
  "-language:implicitConversions")

testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")
parallelExecution in Global := false

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/solar/base58s</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt"</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:solar/base58s.git</url>
    <connection>scm:git:git@github.com:solar/base58s.git</connection>
  </scm>
  <developers>
    <developer>
      <id>solar</id>
      <name>Shinpei Okamura</name>
      <url>https://github.com/solar</url>
    </developer>
  </developers>)
