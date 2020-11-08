package com.easysales.dotty.fp.app.zionomicon.repositories
import zio._
//import zio.console._
import zio.console.{Console, getStrLn, putStrLn}
import zio.blocking.{Blocking, blocking}

import scala.io.Source
import scala.util.Try
import java.io._

def readFile(name:String):ZIO[Blocking with Console, IOException, String] = //URIO[Blocking, String] = //:ZIO[Any, Throwable, String] = //Throwable
  blocking(for
              source <- ZIO.effect(Source.fromFile(name))//.orElseSucceed(null)//.orElse(ZIO.fail(new Throwable("WTF1!!!")))
              //res <- ZIO.fromTry(Try(source.getLines().mkString)).orElseFail(new Throwable("WTF!!!")) //<* ZIO.effect(putStrLn("RELEASE")) //source.close
              //res <- ZIO.fromTry(Try(source.getLines().mkString)).orElse(ZIO.fail(new Throwable("WTF2!!!"))) //<* ZIO.effect(putStrLn("RELEASE")) //source.close
              res <- ZIO.fromTry(Try(try source.getLines().mkString finally source.close))//.orElse(ZIO.succeed("WTF2222!!!!")) //<* ZIO.effect(putStrLn("RELEASE")) //source.close
           yield res).refineToOrDie[IOException].tapCause(c=>putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))//.orElseSucceed("gggg")

def writeFile(name:String, text:String, append:Boolean = true):ZIO[Blocking with Console, IOException, Unit] =
  blocking(for
            pw  <- ZIO.effect(new PrintWriter(new FileOutputStream(new File(name), append )))
            //pw  <- ZIO.effect(new PrintWriter(new File(name)))
            res <- ZIO.fromTry(Try(try pw.println(text) finally pw.close)) //write
           yield res).refineToOrDie[IOException].tapCause(c=>putStrLn(s"Начало трассировки---${c.prettyPrint}---Конец трассировки"))//.orElseSucceed("gggg")
