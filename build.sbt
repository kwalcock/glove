import ReleaseTransformations._

name := "glove_42B_300d"
organization := "org.clulab"

crossPaths := false // This is a resource only and is independent of Scala version.

lazy val core = (project in file("."))

// The resource is presumed to be too large for both GitHub and Maven, so it is copied
// in from an external directory.  This needs to be configured before release.
mappings in (Compile, packageBin) ++= Seq(
  file("./resources/glove.short") -> "org/clulab/glove/glove.42B.300d.txt"
)

publishMavenStyle := true

publishTo := {
  val artifactory = "http://artifactory.cs.arizona.edu:8081/artifactory/"
  val repository = "sbt-release-local"
  val details =
      if (isSnapshot.value) ";build.timestamp=" + new java.util.Date().getTime
      else ""
  val location = artifactory + repository + details

  Some("Artifactory Realm" at location)
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
// credentials += Credentials("Artifactory Realm", "<host>", "<user>", "<password>")
// The above credentials are recorded in ~/.sbt/.credentials as such:
// realm=Artifactory Realm
// host=<host>
// user=<user>
// password=<password>

// Let’s remove any repositories for optional dependencies in our artifact.
pomIncludeRepository := { _ => false }

// These values in scmInfo replace the <scm/> section previously recorded in
// pomExtra so that default values aren't used which then double up in the
// XML and cause a validation error.  This problem was first noted with
// sbt.version=1.1.6
// addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.8")
// addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
// This produced
// <scm>
//     <url>https://github.com/clulab/resource</url>
//     <connection>scm:git:https://github.com/clulab/resource.git</connection>
//     <developerConnection>scm:git:git@github.com:clulab/resource.git</developerConnection>
// </scm>
// that must be automatically generated and a duplicate
// <scm>
//     <url>https://github.com/clulab/resource</url>
//     <connection>https://github.com/clulab/resource</connection>
// </scm>
// Judging from this, the scmInfo is collected automatically, perhaps by
// addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
// However, the developerConnection is undesired, so this is used:
scmInfo := Some(
  ScmInfo(
    url("https://github.com/clulab/glove_42B_300d"),
    "scm:git:https://github.com/clulab/glove_42B_300d.git"
  )
)

// This must be added to add to the pom for publishing.
pomExtra :=
  <url>https://github.com/clulab/glove_42B_300d</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <!--scm>
    <url>https://github.com/clulab/glove_42B_300d</url>
    <connection>https://github.com/clulab/glove_42B_300d</connection>
  </scm-->
  <developers>
    <developer>
      <id>mihai.surdeanu</id>
      <name>Mihai Surdeanu</name>
      <email>mihai@surdeanu.info</email>
    </developer>
  </developers>


releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
