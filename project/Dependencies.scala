import sbt._

import Dependencies._

object Dependencies {

  // scala version
  val scalaOrganization = "org.scala-lang"
  val scalaVersion      = "2.12.6"

  // build tools versions
  val scalaFmtVersion = "1.5.0"

  // libraries versions
  val swaggerVersion = "1.5.20"

  // resolvers
  val resolvers = Seq(
    Resolver sonatypeRepo "public",
    Resolver typesafeRepo "releases"
  )

  // dependencies
  val swaggerCore        = "io.swagger"                   %  "swagger-core"        % swaggerVersion
  val swaggerAnnotations = "io.swagger"                   %  "swagger-annotations" % swaggerVersion
  val swaggerModels      = "io.swagger"                   %  "swagger-models"      % swaggerVersion
  val swaggerJaxrs       = "io.swagger"                   %  "swagger-jaxrs"       % swaggerVersion
  val swaggerAkkaHttp    = "com.github.swagger-akka-http" %% "swagger-akka-http"   % "0.14.0"
  val classUtil          = "org.clapper"                  %% "classutil"           % "1.2.0"
}

trait Dependencies {

  val scalaOrganizationUsed = scalaOrganization
  val scalaVersionUsed = scalaVersion

  val scalaFmtVersionUsed = scalaFmtVersion

  // resolvers
  val commonResolvers = resolvers

  val mainDeps = Seq(swaggerAkkaHttp, classUtil)

  val testDeps = Seq()

  implicit class ProjectRoot(project: Project) {

    def root: Project = project in file(".")
  }

  implicit class ProjectFrom(project: Project) {

    private val moduleDir = "modules"

    def module(dir: String): Project = project in file(s"$moduleDir/$dir")
  }

  implicit class DependsOnProject(project: Project) {

    private val testConfigurations = Set("test", "fun", "it")
    private def findCompileAndTestConfigs(p: Project) =
      (p.configurations.map(_.name).toSet intersect testConfigurations) + "compile"

    private val thisProjectsConfigs = findCompileAndTestConfigs(project)
    private def generateDepsForProject(p: Project) =
      p % (thisProjectsConfigs intersect findCompileAndTestConfigs(p) map (c => s"$c->$c") mkString ";")

    def compileAndTestDependsOn(projects: Project*): Project =
      project dependsOn (projects.map(generateDepsForProject): _*)
  }
}
