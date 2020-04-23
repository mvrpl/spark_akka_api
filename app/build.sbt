name := "WebServer"

version := "1.0"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  val akkaVersion = "2.5.21"
  val akkaHttp = "10.1.7"
  val sparkVersion = "2.4.5"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
  )
}
