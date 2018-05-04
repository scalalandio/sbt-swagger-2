import sbt._
import Settings._

lazy val build = project.root
  .setName("sbt plugin")
  .setDescription("Build of an sbt Swagger 2 plugin")
  .configureRoot
  .settings(noPublishSettings: _*)
  .aggregate(sbtSwagger2)

lazy val sbtSwagger2 = project
  .module("sbt-swagger-2")
  .setName("sbt-swagger-2")
  .setDescription("sbt plugin for generating Swagger JSON schemas during build")
  .setInitialCommand("_")
  .configureModule
  .settings(publishSettings: _*)
  .settings(
    sbtPlugin := true,
    publishMavenStyle := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false
  )

lazy val publishSettings = Seq(
  organization := "io.scalaland",
  homepage := Some(url("https://scalaland.io")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/scalalandio/sbt-swagger-2"), "scm:git:git@github.com:scalalandio/sbt-swagger-2.git")
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra := (
    <developers>
      <developer>
        <id>krzemin</id>
        <name>Piotr Krzemiński</name>
        <url>http://github.com/krzemin</url>
      </developer>
      <developer>
        <id>MateuszKubuszok</id>
        <name>Mateusz Kubuszok</name>
        <url>http://github.com/MateuszKubuszok</url>
      </developer>
    </developers>
  )
)

lazy val noPublishSettings =
  Seq(publish := (()), publishLocal := (()), publishArtifact := false)
