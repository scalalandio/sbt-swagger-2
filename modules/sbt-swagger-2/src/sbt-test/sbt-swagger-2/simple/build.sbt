version := "0.1"
scalaVersion := "2.12.6"

mainClass := Some("simple.Main")

swaggerOutputs += Swagger.Output(
  output = resourceManaged.value / "swagger.json",
  host = "http://localhost",
  schemes = List(Swagger.Scheme.HTTP, Swagger.Scheme.HTTPS),
  info = Swagger.Info(
    title = "Simple API",
    version = version.value,
    description = "Simple description"
  )
)
