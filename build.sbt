name := """vocabstar"""

version := "1.0"

scalaVersion := "2.11.6"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC3",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

Revolver.settings
