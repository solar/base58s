// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "org.sazabi",

  scalaVersion := crossScalaVersions.value.last,
  crossScalaVersions := Seq("2.11.12", "2.12.12", "2.13.3"),

  resolvers ++= (
    ("jitpack" at "https://jitpack.io") ::
    Nil
  ),

  scalacOptions ++=
    "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xfuture" ::
    "-Xlint" ::
    "-Ywarn-value-discard" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-extra-implicit" ::
        "-Ypartial-unification" ::
        "-Yno-adapted-args" ::
        "-Ywarn-infer-any" ::
        "-Ywarn-nullary-override" ::
        "-Ywarn-nullary-unit" ::
        Nil
      case Some((2, 11)) =>
        "-Ypartial-unification" ::
        "-Yno-adapted-args" ::
        "-Ywarn-infer-any" ::
        "-Ywarn-nullary-override" ::
        "-Ywarn-nullary-unit" ::
        Nil
      case _ =>
        Nil
    }
  },

  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),

  initialCommands in console := """
  import org.sazabi.base58._
  """,
)

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .aggregate(base58sJS, base58sJVM)
  .settings(commonSettings)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val base58s = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)
  .settings(commonSettings)
  .settings(
    name         := "base58s",

    libraryDependencies ++=
      "com.github.scalaprops" %%% "scalaprops" % "0.8.0" % "test" ::
    Nil,

    testFrameworks += new TestFramework("scalaprops.ScalapropsFramework"),
    parallelExecution in Global := false
  )
  .jsSettings(
    scalacOptions ++= git.gitHeadCommit.value.map { headCommit =>
      val local = baseDirectory.value.toURI
      val remote = s"https://raw.githubusercontent.com/fdietze/base58s/${headCommit}/"
      s"-P:scalajs:mapSourceURI:$local->$remote"
    }
  )

lazy val base58sJS = base58s.js
lazy val base58sJVM = base58s.jvm



/* releasePublishArtifactsAction := PgpKeys.publishSigned.value */
/* publishMavenStyle := true */

/* publishTo := { */
/*   val nexus = "https://oss.sonatype.org/" */
/*   if (isSnapshot.value) */
/*     Some("snapshots" at nexus + "content/repositories/snapshots") */
/*   else */
/*     Some("releases" at nexus + "service/local/staging/deploy/maven2") */
/* } */

/* publishArtifact in Test := false */

/* pomIncludeRepository := { _ => false } */

/* pomExtra := ( */
/*   <url>https://github.com/solar/base58s</url> */
/*   <licenses> */
/*     <license> */
/*       <name>Apache 2</name> */
/*       <url>http://www.apache.org/licenses/LICENSE-2.0.txt"</url> */
/*       <distribution>repo</distribution> */
/*     </license> */
/*   </licenses> */
/*   <scm> */
/*     <url>git@github.com:solar/base58s.git</url> */
/*     <connection>scm:git:git@github.com:solar/base58s.git</connection> */
/*   </scm> */
/*   <developers> */
/*     <developer> */
/*       <id>solar</id> */
/*       <name>Shinpei Okamura</name> */
/*       <url>https://github.com/solar</url> */
/*     </developer> */
/*   </developers>) */
