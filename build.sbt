import sbt.Keys._

crossPaths := false

resolvers += "Local Maven Repository" at s"file://${Path.userHome.absolutePath}/.m2/repository"

lazy val scalaSwing = RootProject(uri("https://github.com/scala/scala-swing.git"))

lazy val mundane = project.in(file("."))
  .settings(
    name := "mundane",
    organization := "com.github.moneydance",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(

      // For compiling only (not packaged in assembly)

      "com.moneydance" % "moneydance-dev" % "4.0" % "provided",

      // Production

      "com.typesafe.play" %% "play-json" % "2.5.8",
      "com.miglayout" % "miglayout-swing" % "5.0",

      // Tests

      "org.scalatest" %% "scalatest" % "3.0.0" % "test"
    ),

    assemblyJarName in assembly := "mundane.jar", // has to be synched with sbt.jar.name in build.xml
    test in assembly := {}
  )
  .dependsOn(scalaSwing)

