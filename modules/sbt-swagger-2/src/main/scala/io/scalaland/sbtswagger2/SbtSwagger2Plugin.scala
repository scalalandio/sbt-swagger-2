package io.scalaland.sbtswagger2

import io.swagger.annotations
import org.clapper.classutil.{ ClassFinder, ClassUtil }
import sbt._
import sbt.Keys._
import sbt.internal.PluginManagement.PluginClassLoader
import sbt.plugins.JvmPlugin

object SbtSwagger2Plugin extends AutoPlugin {

  override def trigger  = allRequirements
  override def requires = JvmPlugin

  object autoImport {

    val swaggerVersion       = settingKey[String]("Version of Swagger Annotations that should be added to project")
    val swaggerJsr311Version = settingKey[String]("Version of JSR311 that should be added to project")
    val swaggerOutputs       = settingKey[Seq[SwaggerOutput]]("Configurations of all intended Swagger outputs")
    val swaggerGenerate      = taskKey[Seq[File]]("Generate Swagger outputs")

    object Swagger {

      type Output = _root_.io.scalaland.sbtswagger2.SwaggerOutput
      val Output = _root_.io.scalaland.sbtswagger2.SwaggerOutput

      type Contact = _root_.com.github.swagger.akka.model.Contact
      val Contact = _root_.com.github.swagger.akka.model.Contact

      type ExternalDocs = _root_.io.swagger.models.ExternalDocs

      type Info = _root_.com.github.swagger.akka.model.Info
      val Info = _root_.com.github.swagger.akka.model.Info

      type License = _root_.com.github.swagger.akka.model.License
      val License = _root_.com.github.swagger.akka.model.License

      type Scheme = _root_.io.swagger.models.Scheme
      object Scheme {
        val HTTP  = _root_.io.swagger.models.Scheme.HTTP
        val HTTPS = _root_.io.swagger.models.Scheme.HTTPS
        val WS    = _root_.io.swagger.models.Scheme.WS
        val WSS   = _root_.io.swagger.models.Scheme.WSS
      }

      type SecuritySchemeDefinition = _root_.io.swagger.models.auth.SecuritySchemeDefinition
      type ApiKeyAuthDefinition     = _root_.io.swagger.models.auth.ApiKeyAuthDefinition
      type BasicAuthDefinition      = _root_.io.swagger.models.auth.BasicAuthDefinition
      type OAuth2Definition         = _root_.io.swagger.models.auth.OAuth2Definition
    }
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    swaggerVersion := "1.5.19",
    swaggerJsr311Version := "1.1.1",
    swaggerOutputs := Seq.empty,
    swaggerGenerate := {
      val classPath      = (fullClasspath in Compile).value
      val inputDirectory = (classDirectory in Compile).value
      val log            = streams.value.log
      val apiClass       = classOf[annotations.Api]
      val apiSignature   = ClassUtil.classSignature(apiClass)

      val pluginClassLoader = apiClass.getClassLoader.asInstanceOf[PluginClassLoader]
      pluginClassLoader.add(classPath.files.map(_.toURI.toURL))
      log.info(s"Looking for compiled project classes in ${inputDirectory.toURI.toURL} ...")

      val classes = ClassFinder(Seq(inputDirectory)).getClasses
        .filter(_.annotations.exists(_.descriptor == apiSignature))
        .map { classInfo =>
          Class.forName(classInfo.name, false, pluginClassLoader)
        }
        .toSet

      val output = swaggerOutputs.value.map { swaggerConfig =>
        log.info(s"Generating Swagger JSON in ${swaggerConfig.output} ...")
        IO.write(swaggerConfig.output, swaggerConfig.generator(classes).generateSwaggerJson)
        swaggerConfig.output
      }
      log.info(s"Done generating ${output.size} Swagger JSON.")
      output
    },
    libraryDependencies += "io.swagger" % "swagger-annotations" % swaggerVersion.value,
    libraryDependencies += "javax.ws.rs" % "jsr311-api" % swaggerJsr311Version.value
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}
