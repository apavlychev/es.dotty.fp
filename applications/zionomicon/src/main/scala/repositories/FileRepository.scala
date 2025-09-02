package com.easysales.dotty.fp.app.zionomicon.repositories
import zio.{Console, Exit, Task, UIO, ZIO}
import zio.Console.*

import scala.io.Source
//import zio.Blocking
//import zio.blocking.Blocking
//import zio.blocking.blocking
//import zio.console.{Console, getStrLn, putStrLn}
//import zio.blocking.{Blocking, blocking}

import scala.io.Source
import scala.util.Try
import java.io._

def readFile(name: String): ZIO[Any, IOException, String] =
  // ZIO.fromAutoCloseable
  ZIO
    .acquireReleaseWith(ZIO.attempt(Source.fromFile(name)))(s => if (s eq null) Exit.unit else ZIO.succeed(s.close()))(
      s => ZIO.attemptBlockingIO(s.getLines().mkString)
    )
    .refineToOrDie[IOException]
  // .tapCause(c => putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))

def writeFile(name: String, text: String, append: Boolean = true): ZIO[Any, IOException, Unit] =
  ZIO
    .acquireReleaseWith(ZIO.attempt(new PrintWriter(new FileOutputStream(new File(name), append))))(s =>
      ZIO.succeed(s.close)
    )(s => ZIO.attempt(s.println(text)))
    .refineToOrDie[IOException]
  // .tapCause(c => putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))
