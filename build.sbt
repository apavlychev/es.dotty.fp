//Deps
lazy val junit= "com.novocode" % "junit-interface" % "0.11" % "test"
lazy val typesafe = "com.typesafe" % "config" % "1.4.0"
lazy val scallop = "org.rogach" %% "scallop" % "3.5.1"
lazy val zioVersion = "1.0.3"
lazy val zioCore =  "dev.zio" %% "zio"               % zioVersion
lazy val zioTest = "dev.zio" %% "zio-test"          % zioVersion % Test
lazy val zioSbt = "dev.zio" %% "zio-test-sbt"      % zioVersion % Test
lazy val zioJunit = "dev.zio" %% "zio-test-junit"    % zioVersion % Test
lazy val zioMagnolia = "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
lazy val monadic = "com.olegpy" %% "better-monadic-for" % "0.3.1"
 
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "3.0.0-M3"
ThisBuild / organization := "com.easysales"

//Тестирование нового синтакса Scala 3
lazy val syntax = (project in file ("applications/syntax")).
  settings(
      name := "com.easysales.dotty.fp.app.syntax",
      mainClass := Some("com.easysales.dotty.fp.app.syntax.Main"),
      libraryDependencies ++= Seq(
        junit,
        typesafe,
        scallop
      ),
      libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value)), //https://github.com/lampepfl/dotty-example-project
      //testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    //    test in assembly := {},
//    assemblyJarName in assembly := s"es-dotty=fp-app-syntax.jar",
//    assemblyMergeStrategy in assembly := {
//      //case PathList("logback.xml") => MergeStrategy.discard
//      case PathList("application.conf") => MergeStrategy.concat
//      case PathList("reference.conf") => MergeStrategy.concat
//      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
//      case _ => MergeStrategy.first
//    }
  )
  //.enablePlugins(AssemblyPlugin)
  .dependsOn(common)

//Работа по Zio (книга zionomicon)
lazy val zio = (project in file ("applications/zionomicon")).
  settings(
    name := "com.easysales.dotty.fp.app.zionomicon",
    mainClass := Some("com.easysales.dotty.fp.app.zionomicon.Main"),
    addCompilerPlugin(monadic),    
    libraryDependencies ++= Seq(
      junit,
      typesafe,
      scallop,
      zioCore,
      zioTest,
      zioSbt,
      zioJunit,
      zioMagnolia
    ),
    libraryDependencies := libraryDependencies.value.map(_.withDottyCompat(scalaVersion.value)),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
  //.enablePlugins(AssemblyPlugin)
  .dependsOn(common)

//
////Инфраструктура
lazy val common = (project in file ("infrastructure/common")).
  settings(
    name := "com.easysales.dotty.fp.inf.common",
  )




