package io.scalaland.sbtswagger2

import com.github.swagger.akka.{ SwaggerGenerator, SwaggerScalaModelConverter }
import com.github.swagger.akka.model._
import io.swagger.converter.ModelConverters
import sbt.{ file, File }
import _root_.io.swagger.models.{ Info => _, _ }
import _root_.io.swagger.models.auth._

object SwaggerOutput {

  class Generator(self: SwaggerOutput, classes: Set[Class[_]]) extends SwaggerGenerator {
    ModelConverters.getInstance.addConverter(new SwaggerScalaModelConverter)

    override val apiClasses:                Set[Class[_]]                         = classes.filter(self.inputFilter)
    override val host:                      String                                = self.host
    override val basePath:                  String                                = self.basePath
    override val info:                      Info                                  = self.info
    override val schemes:                   List[Scheme]                          = self.schemes
    override val securitySchemeDefinitions: Map[String, SecuritySchemeDefinition] = self.securitySchemeDefinitions
    override val externalDocs:              Option[ExternalDocs]                  = self.externalDocs
    override val vendorExtensions:          Map[String, Object]                   = self.vendorExtensions
    override val unwantedDefinitions:       Seq[String]                           = self.unwantedDefinitions
  }
}

final case class SwaggerOutput(
  inputFilter:               Class[_] => Boolean                   = _ => true,
  output:                    File                                  = file("swagger.json"),
  host:                      String                                = "",
  basePath:                  String                                = "/",
  info:                      Info                                  = Info(),
  schemes:                   List[Scheme]                          = List(Scheme.HTTP),
  securitySchemeDefinitions: Map[String, SecuritySchemeDefinition] = Map.empty,
  externalDocs:              Option[ExternalDocs]                  = None,
  vendorExtensions:          Map[String, Object]                   = Map.empty,
  unwantedDefinitions:       Seq[String]                           = Seq.empty
) {

  def generator(classLoader: ClassLoader, classes: Set[Class[_]]): SwaggerGenerator =
    classLoader
      .loadClass(classOf[SwaggerOutput.Generator].getName)
      .getConstructors
      .head
      .newInstance(this, classes)
      .asInstanceOf[SwaggerOutput.Generator]
}
