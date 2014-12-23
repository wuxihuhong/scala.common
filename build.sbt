name := "scala.common"

organization := "com.github.wuxihuhong"

version := "1.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation","-unchecked")

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
  "javax.servlet" % "javax.servlet-api" % "3.0.1"
)


