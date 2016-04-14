name := """bot"""

version := "0.1"

scalaVersion := "2.11.7"

val akkaVersion: String = "2.4.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe" % "config" % "1.3.0",
  "org.twitter4j" % "twitter4j-core" % "4.0.4"
)

fork in run := true
