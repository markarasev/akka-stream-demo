scalaVersion := "2.12.3"

val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
  ,"com.typesafe.akka" %% "akka-stream-kafka" % "0.17"
  ,"org.scalacheck" %% "scalacheck" % "1.13.4"
  ,"com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
  ,"org.scalatest" %% "scalatest" % "3.0.1" % Test
)