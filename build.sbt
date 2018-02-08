name := "news-server"
 
version := "1.0" 
      
lazy val `news-server` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val catsVersion = "1.0.1"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test ,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.4",
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-kernel" % catsVersion,
  "org.typelevel" %% "cats-macros" % catsVersion)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      