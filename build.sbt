name := """nakadi-klients-play-json"""

version := "0.1.0"

organization := "org.zalando.nakadi.client"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.zalando.nakadi.client" % "nakadi-klients" % "2.0",
  "com.typesafe.play"         %% "play-json"     % "2.5.8"
)

resolvers += "zalando-nexus-releases" at "https://maven.zalando.net/content/repositories/releases/"
resolvers += "zalando-nexus-snapshots" at "https://maven.zalando.net/content/repositories/snapshots/"

scalafmtConfig in ThisBuild := Some(file(".scalafmt.conf"))

publishMavenStyle := true

// Publish snapshots to a different repository
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

pomExtra := <url>https://github.com/zalando-incubator/nakadi-klients-play-json</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>https://opensource.org/licenses/Apache-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/zalando-incubator/nakadi-klients-play-json</url>
    <connection>scm:git:git@github.com:zalando-incubator/nakadi-klients-play-json.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mdedetrich</id>
      <name>Matthew de Detrich</name>
      <email>matthew.de.detrich@zalando.de</email>
    </developer>
  </developers>
