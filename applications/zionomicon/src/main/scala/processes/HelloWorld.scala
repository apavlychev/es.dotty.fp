package com.easysales.dotty.fp.app.zionomicon.processes

import java.io.IOException

import zio.ZIO
import zio.console.{Console, getStrLn, putStrLn}

//Hello world!
lazy val helloWorld: ZIO[Console, IOException, Unit] =
  for
    _    <- putStrLn("What is your name?")
    name <- getStrLn
    out  <- putStrLn(s"Hello $name!") 
  yield out