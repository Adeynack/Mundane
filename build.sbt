organization := "com.github.moneydance"
name := "mundane"
version := "1.0-SNAPSHOT"

autoScalaLibrary := false
crossPaths := false

resolvers += "Local Maven Repository" at s"file://${Path.userHome.absolutePath}/.m2/repository"

libraryDependencies ++= Seq(

  // For compiling only (not packaged in assembly)
  "com.moneydance" % "moneydance-dev" % "4.0" % "provided",

  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.1",
  "com.miglayout" % "miglayout-swing" % "5.0",

  // Tests
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

assemblyJarName in assembly := "mundane.jar" // has to be synched with sbt.jar.name in build.xml

test in assembly := {}
