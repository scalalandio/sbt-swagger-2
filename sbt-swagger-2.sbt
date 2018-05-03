import sbt._
import Settings._

lazy val build = project.root
  .setName("sbt plugin")
  .setDescription("Build of an sbt Swagger 2 plugin")
  .configureRoot
  .aggregate(sbtSwagger2)

lazy val sbtSwagger2 = project
  .module("sbt-swagger-2")
  .setName("sbt-swagger-2")
  .setDescription("sbt plugin for generating Swagger JSON schemas during build")
  .setInitialCommand("_")
  .configureModule
  .settings(
    sbtPlugin := true,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
  )
