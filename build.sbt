import com.typesafe.sbt.packager.docker._
import sbt.Keys.mappings

organization := "com.urdnot.iot.utils"

name := "HomeApiService"

version := "1.0.1"

val scalaMajorVersion = "2.13"
val scalaMinorVersion = "2"

scalaVersion := scalaMajorVersion.concat("." + scalaMinorVersion)

libraryDependencies ++= {
  val sparyJsonVersion = "10.1.12"
  val akkaHttpVersion = "10.1.12"
  val logbackClassicVersion = "1.2.3"
  val scalatestVersion = "3.1.1"
  val akkaVersion = "2.6.5"
  val scalaLoggingVersion = "3.9.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % sparyJsonVersion,
    "ch.qos.logback" % "logback-classic" % logbackClassicVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  )
}

enablePlugins(DockerPlugin)

mainClass in (Compile, assembly) := Some("com.urdnot.iot.utils.HomeApiService")

assemblyJarName := s"${name.value}.v${version.value}.jar"
val meta = """META.INF(.)*""".r

mappings in(Compile, packageBin) ~= {
  _.filterNot {
    case (_, name) => Seq("application.conf").contains(name)
  }
}

assemblyMergeStrategy in assembly := {
  case n if n.endsWith(".properties") => MergeStrategy.concat
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("resources/application.conf") => MergeStrategy.discard
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}

dockerBuildOptions += "--no-cache"
dockerUpdateLatest := true
dockerPackageMappings in Docker += file(s"target/scala-2.13/${assemblyJarName.value}") -> s"opt/docker/${assemblyJarName.value}"
mappings in Docker += file("src/main/resources/application.conf") -> "opt/docker/application.conf"
mappings in Docker += file("src/main/resources/logback.xml") -> "opt/docker/logback.xml"
dockerExposedPorts := Seq(8081)

dockerCommands := Seq(
  Cmd("FROM", "openjdk:11-jdk-slim"),
  Cmd("LABEL", s"""MAINTAINER="Jeffrey Sewell""""),
  Cmd("COPY", s"opt/docker/${assemblyJarName.value}", s"/opt/docker/${assemblyJarName.value}"),
  Cmd("COPY", "opt/docker/application.conf", "/var/application.conf"),
  Cmd("COPY", "opt/docker/logback.xml", "/var/logback.xml"),
  Cmd("ENV", "CLASSPATH=/opt/docker/application.conf:/opt/docker/logback.xml"),
  Cmd("ENTRYPOINT", s"java -cp /opt/docker/${assemblyJarName.value} com.urdnot.iot.api.HomeApiService")
)
// sbt clean
// sbt assembly
// sbt docker:publishLocal
// docker image tag homeapiservice:latest intel-server-03:5000/homeapiservice
// docker image push intel-server-03:5000/homeapiservice