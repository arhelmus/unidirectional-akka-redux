name := "tweet-the-world-backend"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.4.7"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaVersion,

    "com.github.fomkin" %% "pushka-json" % "0.6.0",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )
}

// Required by Pushka
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)