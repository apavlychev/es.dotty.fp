package com.easysales.dotty.fp.app.zionomicon.repositories
import zio.{ZIO, Task, UIO}
import zio.console.{Console, getStrLn, putStrLn}
import zio.blocking.{Blocking, blocking}

import scala.io.Source
import scala.util.Try
import java.io._

def readFile(name:String):ZIO[Blocking with Console, IOException, String] =
  blocking(Task(Source.fromFile(name)).bracket(s=> UIO(s.close))(s=> Task(s.getLines().mkString)))
    .refineToOrDie[IOException].tapCause(c=>putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))

def writeFile(name:String, text:String, append:Boolean = true):ZIO[Blocking with Console, IOException, Unit] =
  blocking(Task(new PrintWriter(new FileOutputStream(new File(name), append ))).bracket(s=> UIO(s.close))(s=>Task(s.println(text))))
    .refineToOrDie[IOException].tapCause(c=>putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))
