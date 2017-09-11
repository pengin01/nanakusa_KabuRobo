name := "KabuRobo"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.7.3"
  ,"org.slf4j" % "slf4j-nop" % "1.6.4"
  ,"com.github.tototoshi" %% "scala-csv" % "1.3.4"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
