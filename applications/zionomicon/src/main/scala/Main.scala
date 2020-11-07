package com.easysales.dotty.fp.app.zionomicon

import java.io.IOException
import java.time.OffsetDateTime

import emails.{Email, sendEmail}
import models.Person
import zio.{App, ExitCode, Ref, URIO, ZEnv, ZIO}
import zio.blocking.{Blocking, blocking}
import zio.console.{Console, getStrLn, putStrLn, putStrLnErr}
import repositories.{createPerson, getPersonById, readFile, savePerson, writeFile}
import zio.clock.{Clock, currentDateTime}
import zio.random.Random
import validators.{SavedError, validatePerson}
import transactions._
import zio.duration._
import processes.{_}
import scala.io.Source

object Main extends App:
  
  //Запуск подпрограмм
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    for 
      _    <- putStrLn( s"Запуск приложения Zionomicon - name: ${Settings.name}, descr: ${Settings.descr}, age: ${Settings.age}")
      conf = Args(args)
      _    <- putStrLn(s"${conf.getProcess.descr}")
      code <- conf.getProcess match 
        case Types.HelloWorld => helloWorld.exitCode
        case Types.HandleErrors => retryCopyFile.exitCode
        case Types.HandleFibers => makeAllPersons.exitCode
        case null => ZIO.fail("Не найден сценарий").exitCode 
    yield code
