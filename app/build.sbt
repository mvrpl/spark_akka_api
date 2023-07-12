name := "Spark Rest API"

version := "1.0"

scalaVersion := "2.12.17"

libraryDependencies ++= {
  val akkaVersion = "2.8.3"
  val akkaHttp = "10.5.2"
  val sparkVersion = "3.4.1"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
  )
}
