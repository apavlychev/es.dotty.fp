package com.easysales.dotty.fp.app.zionomicon.processes

import com.easysales.dotty.fp.app.zionomicon.repositories.{readFile, writeFile}
import zio.{URIO, ZIO}
//import zio.blocking.Blocking
import zio.Console //, getStrLn, putStrLn, putStrLnErr}
//import zio.Console._
import com.easysales.dotty.fp.app.zionomicon.utils.ConsoleExt.*
//import scala.language.postfixOps

import scala.io.Source

//Тестовый файл /home/sirius/Desktop/dockers
//Императивный код (программа может упасть в любом месте с Exception):
//Будет ли данный код работать стабильно ? Какие есть гарантии его надежности? Каким образом обработать нештатные ситуации ?
//Обычное решение - отладка, тестирование и Unit-тесты. Но они не дают полной гарантии.
def imperativeCopyFile: Unit = {
  println("Введите путь к файлу:")
  val fileName = scala.io.StdIn.readLine()
  val data     = Source.fromFile(fileName).getLines().mkString
  val prodData = "<---begin---" + data + "---end--->"
  println("Содержимое файла: ")
  println(s"$prodData")
  writeFile(fileName + "_copy", prodData)
  println(s"Создана копия файла ${fileName}_copy")
}

//Теперь давайте предположим, что мы хотим, чтобы наш код не падал с Exception, а вместо этого возвращал контролируемую ошибку типа ReadError/WriteError
//с описанием причины отказа во всех возможных случаях. Также в случае ошибки необходимо реализовать логику повторного запроса файла.
//При попытке императивной реализации нам придется оборачивать каждую строчку кода в try/catch, что приводит к потере читабельности и адскому болерплейту.
//При ФП подходе мы должны обернуть все наши value в типы ZIO, а также заменить императивную композицию - функциональной.
//Благодаря ф-ой композиции (map, flatMap) и типам (ZIO) компилятор может контролировать вашу программу через вывод типов, т.к. программа представляет
//непрерывный поток преобразований одного типа в другой через "чистые" ф-ии (ControlFlow => DataFlow)
//Таким образом  происходит самовалидация и мы можем дать экстремальные гарантии надежности кода и существенно облегчить рефакторинг и поддержку.

//Возможные классы ошибок
case class FileReadError(message: String)
case class FileWriteError(message: String)

lazy val copyFile: ZIO[Any, FileReadError | FileWriteError, Unit] =
  for {
    _        <- printLine("Введите путь к файлу:")
    fileName <- readLine.orDie
    data     <- readFile(fileName).orElseFail(FileReadError(s"Не удалось прочитать файл: $fileName"))
    prodData  = "<---begin---" + data + "---end--->"
    _        <- printLine(s"Содержимое файла: ")
    _        <- printLine(s"$prodData!")
    _        <- writeFile(s"${fileName}_copy", prodData)
                  .orElseFail(FileWriteError(s"К сожалению не удалось создать copy файл: ${fileName}_copy"))
    _        <- printLine(s"Создана копия файла ${fileName}_copy")
  } yield ()

lazy val retryCopyFile: ZIO[Any, Nothing, Unit] =
  copyFile.foldZIO(
    error => printLineError(s"Произошла ошибка: $error. Повторите ввод") *> retryCopyFile,
    _ => ZIO.succeed(())
  )
