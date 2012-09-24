organization := "com.osinka.camel"

name := "camel-scala-extra"

version := "1.4.1-SNAPSHOT"

homepage := Some(url("https://github.com/osinka/camel-scala-extra"))

startYear := Some(2011)

scalaVersion := "2.9.2"

licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

organizationName := "Osinka"

description := """Extra Scala helpers for Apache Camel"""

scalacOptions += "-unchecked"

libraryDependencies ++= Seq(
  "org.apache.camel" % "camel-core" % "2.10.1",
  "org.apache.camel" % "camel-scala" % "2.10.1",
  "org.scalatest" %% "scalatest" % "1.8" % "test",
  "junit" % "junit" % "4.10" % "test",
  "org.slf4j" % "slf4j-simple" % "1.7.1" % "test"
)

credentials += Credentials(Path.userHome / ".ivy2" / "credentials_sonatype")

pomIncludeRepository := { x => false }

publishTo <<= (version) { version: String =>
  if (version.trim endsWith "SNAPSHOT")
    Some(Resolver.file("file", file(Path.userHome.absolutePath+"/.m2/repository")))
  else
    Some("Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/")
}

pomExtra := <xml:group>
    <developers>
      <developer>
        <id>alaz</id>
        <email>azarov@osinka.com</email>
        <name>Alexander Azarov</name>
        <timezone>+4</timezone>
      </developer>
    </developers>
    <scm>
      <connection>scm:git:git://github.com/osinka/camel-scala-extra.git</connection>
      <developerConnection>scm:git:git@github.com:osinka/camel-scala-extra.git</developerConnection>
      <url>http://github.com/osinka/camel-scala-extra</url>
    </scm>
    <issueManagement>
      <system>github</system>
      <url>http://github.com/osinka/camel-scala-extra/issues</url>
    </issueManagement>
  </xml:group>
