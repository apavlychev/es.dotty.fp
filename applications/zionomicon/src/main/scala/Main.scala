package com.easysales.dotty.fp.app.zionomicon

import zio.{Console, ZIOAppDefault}
import zio.Console.*
import processes.*

object Main extends ZIOAppDefault {

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
            }
  } yield ()
}
