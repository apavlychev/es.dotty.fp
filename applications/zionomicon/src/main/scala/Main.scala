package com.easysales.dotty.fp.app.zionomicon

import java.io.IOException
import java.time.OffsetDateTime
import emails.{Email, sendEmail}
import models.Person
import zio.{Clock, Console, ExitCode, Random, Ref, URIO, ZEnvironment, ZIO, ZIOApp, ZIOAppArgs, ZIOAppDefault}
import zio.Console.*
import repositories.{createPerson, getPersonById, readFile, savePerson, writeFile}
import validators.{SavedError, validatePerson}
import transactions.*
import processes.*
import zio.ZIOAppArgs.*

import scala.io.Source

object Main extends ZIOAppDefault { // App

  def run = for {
    _    <- printLine(
              s"Запуск приложения Zionomicon - name: ${Settings.name}, descr: ${Settings.descr}, age: ${Settings.age}"
            )
    conf <- getArgs.map(args => Args(args.toList))
    _    <- printLine(s"${conf.getProcess.descr}")
    code <- conf.getProcess match {
              case Types.HelloWorld   => helloWorld
              case Types.HandleErrors => retryCopyFile
              case Types.HandleFibers => makeAllPersons
              // case null             => ZIO.fail(new Throwable("Не найден сценарий"))
            }
  } yield ()

//  def run: ZIO[ZIOAppArgs, IOException, Unit] = for {
//    args <- getArgs
//    _    <-
//      if (args.isEmpty)
//        Console.printLine("Please provide your name as an argument")
//      else
//        Console.printLine(s"Hello, ${args.head}!")
//  } yield ()

//  val environment: ZEnvironment[Console & Clock & Random] =
//    ZEnvironment[Console, Clock, Random](
//      Console.ConsoleLive,
//      Clock.ClockLive,
//      Random.RandomLive
//    )
//
//  def run: ZIO[Any, Throwable, Unit] =
//    (for {
//      _    <- printLine(
//                s"Запуск приложения Zionomicon - name: ${Settings.name}, descr: ${Settings.descr}, age: ${Settings.age}"
//              ).!
//      conf <- getArgs.map(args => Args(args.toList))
//      _    <- printLine(s"${conf.getProcess.descr}").!
//      code <- conf.getProcess match
//                case Types.HelloWorld   => helloWorld
//                case Types.HandleErrors => retryCopyFile
//                case Types.HandleFibers => makeAllPersons
//                case null               => ZIO.fail(new Throwable("Не найден сценарий"))
//    } yield code).provideLayer(bootstrap).provideEnvironment(environment) // [DefEnv with ZIOAppArgs]
}
