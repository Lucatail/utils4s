name := "Utils4s"

version := "0.1"

scalaVersion := "2.13.0"

idePackagePrefix := Some("org.altynai.utils4s")

libraryDependencies ++= Seq(
  // httpclient
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.2",
  // logger
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
)