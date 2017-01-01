name := "homeland"

version := "1.0"

lazy val `homeland` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(jdbc, cache, ws, specs2 % Test)

libraryDependencies ++= Seq(
  "org.webjars.bower" % "bootstrap-sass" % "3.3.6",
  "org.webjars" % "jquery" % "2.1.3",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "com.typesafe.play" %% "play-slick" % "2.0.0"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// if localhost has a lot of cookies
PlayKeys.devSettings := Seq("play.server.netty.maxHeaderSize" -> "16384")