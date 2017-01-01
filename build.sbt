name := "homeland"

version := "1.0"

lazy val `homeland` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(jdbc, cache, ws, specs2 % Test)

libraryDependencies += "org.webjars.bower" % "bootstrap-sass" % "3.3.6"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// if localhost has a lot of cookies
PlayKeys.devSettings := Seq("play.server.netty.maxHeaderSize" -> "16384")