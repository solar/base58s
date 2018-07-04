// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "org.sazabi",

  scalaVersion := "2.12.6",
  crossScalaVersions := Seq("2.11.12", "2.12.6"),

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
    "-Ypartial-unification" ::
    "-Yno-adapted-args" ::
    "-Ywarn-infer-any" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-nullary-override" ::
    "-Ywarn-nullary-unit" ::
    Nil,

  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) =>
        "-Ywarn-extra-implicit" ::
        Nil
      case _ =>
        Nil
    }
  },

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7"),

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
      "com.github.scalaprops" %%% "scalaprops" % "0.5.5" % "test" ::
    Nil,

    testFrameworks += new TestFramework("scalaprops.ScalapropsFramework"),
    parallelExecution in Global := false
  )
  .jsSettings(
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
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
