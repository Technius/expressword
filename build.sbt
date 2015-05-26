name := """expressword"""

version := "1.0"

scalaVersion := "2.11.6"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC3",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.json4s" %% "json4s-ext" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

enablePlugins(JavaServerAppPackaging)

maintainer in Docker := "Bryan Tan <techniux@gmail.com>"

dockerBaseImage := "williamyeh/java7:latest"

dockerExposedPorts := Seq(9000)

scalacOptions in Global ++= Seq(
  "-target:jvm-1.7"
)

javacOptions in Global ++= Seq("-source", "1.7", "-target", "1.7")

Revolver.settings
