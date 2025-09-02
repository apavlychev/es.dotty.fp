package com.easysales.dotty.fp.app.zionomicon.utils

import zio.{Console, IO, UIO}

import java.io.IOException

object ConsoleExt {
  def printLine(line: String): UIO[Unit] =
    Console.printLine(line).orDie

  def printLineError(line: String): UIO[Unit] =
    Console.printLineError(line).orDie

  def readLine: IO[IOException, String] = Console.readLine
}
