name := "scala-common"

organization := "com.github.wuxihuhong"

version := "1.0.0-M6-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-unchecked","-optimise","-Ylog:inline")

javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6", "-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= Seq(
  "org.springframework" % "spring-context" % "4.1.2.RELEASE",
  "org.springframework" % "spring-tx" % "4.1.2.RELEASE",
  "org.springframework" % "spring-orm" % "4.1.2.RELEASE",
  "org.springframework" % "spring-test" % "4.1.2.RELEASE" % "test",
  "org.springframework" % "spring-webmvc" % "4.1.2.RELEASE",
  "org.hibernate" % "hibernate-core" % "4.3.7.Final",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "org.slf4j" % "slf4j-log4j12" % "1.7.7",
  "org.scala-lang" % "scala-compiler" % "2.10.4",
  "log4j" % "log4j" % "1.2.17",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.3",
  "com.google.code.gson" % "gson" % "2.2.2",
  "junit" % "junit" % "4.11" % "test",
  "com.alibaba" % "druid" % "1.0.11" % "test",
  "mysql" % "mysql-connector-java" % "5.1.18" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.2.2" % "test",
  "org.scala-lang" % "jline" % "2.10.4",
  "javax.servlet" % "javax.servlet-api" % "3.0.1",
  "net.liftweb" %% "lift-json" % "2.5",
  "org.hibernate" % "hibernate-search-orm" % "5.0.0.Final",
  "javax.enterprise" % "cdi-api" % "1.0-SP4",
  "org.apache.lucene" % "lucene-queryparser" % "4.10.2"
)

publishMavenStyle := true

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
//webInfClasses in webapp := true
//
//jetty(port = 9000)
