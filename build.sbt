name := "homeland"

version := "1.0"

lazy val `homeland` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(cache, ws, specs2 % Test)

libraryDependencies ++= Seq(
  "org.webjars.bower" % "bootstrap-sass" % "3.3.6",
  "org.webjars" % "jquery" % "2.1.3",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
  "com.h2database" % "h2" % "1.4.193",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "net.ceedubs" % "ficus_2.11" % "1.1.2",
  "com.typesafe.play" %% "play-mailer" % "5.0.0"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "atlassian" at "https://maven.atlassian.com/content/repositories/atlassian-public/"

// if localhost has a lot of cookies
PlayKeys.devSettings := Seq("play.server.netty.maxHeaderSize" -> "16384")