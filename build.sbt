name := "hello-world"

version := "0.1"

scalaVersion := "2.12.10"

val samzaVersion = "1.4.0"

val samzaCore = "org.apache.samza" %% "samza-core" % samzaVersion
val samzaKafka = "org.apache.samza" %% "samza-kafka" % samzaVersion
// https://mvnrepository.com/artifact/org.apache.samza/samza-test
val samzaTest= "org.apache.samza" %% "samza-test" % samzaVersion
val typesafe = "com.typesafe" % "config" % "1.4.0"
val scalatest = "org.scalatest" % "scalatest_2.12" % "3.1.0" % "test"


libraryDependencies ++= Seq(
  samzaCore
  , samzaKafka
  ,samzaTest
  , typesafe
  , scalatest
)
