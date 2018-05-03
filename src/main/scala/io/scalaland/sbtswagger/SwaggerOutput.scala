package io.scalaland.sbtswagger

import com.github.swagger.akka.SwaggerGenerator
import com.github.swagger.akka.model._
import sbt.{ file, File }
import _root_.io.swagger.models.{ Info => _, _ }
import _root_.io.swagger.models.auth._

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
) { self =>

  def generator(classes: Set[Class[_]]): SwaggerGenerator =
    new SwaggerGenerator {
      override val apiClasses:                Set[Class[_]]                         = classes.filter(inputFilter)
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
