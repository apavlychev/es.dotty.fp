package com.easysales.dotty.fp.app.zionomicon.processes

import java.io.IOException

import zio.ZIO
import zio.Console

//Hello world!
lazy val helloWorld: ZIO[Any, IOException, Unit] =
  for {
    _    <- Console.printLine("What is your name?")
    name <- Console.readLine
    out  <- Console.printLine(s"Hello $name!")
  } yield out
