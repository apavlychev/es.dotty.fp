package com.easysales.dotty.fp.app.zionomicon.repositories
import zio.{Exit, IO, ZIO}
import scala.io.Source
import java.io._
import zio.Console.*

def readFile(name: String): IO[IOException, String] =
  ZIO
    .acquireReleaseWith(ZIO.attempt(Source.fromFile(name)))(s => if (s eq null) Exit.unit else ZIO.succeed(s.close())) {
      s => ZIO.attemptBlockingIO(s.getLines().mkString)
    }
    .refineToOrDie[IOException]
    .tapErrorCause(c => printLine(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))

def writeFile(name: String, text: String, append: Boolean = true): IO[IOException, Unit] =
  ZIO
    .acquireReleaseWith(ZIO.attempt(new PrintWriter(new FileOutputStream(new File(name), append))))(s =>
      if (s eq null) Exit.unit else ZIO.succeed(s.close())
    )(s => ZIO.attempt(s.println(text)))
    .refineToOrDie[IOException]
    .tapErrorCause(c => printLine(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))
