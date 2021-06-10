package com.easysales.dotty.fp.app.zionomicon.processes

import com.easysales.dotty.fp.app.zionomicon.repositories.{readFile, writeFile}
import zio.{URIO, ZIO}
import zio.blocking.Blocking
import zio.console.{Console, getStrLn, putStrLn, putStrLnErr}
//import scala.language.postfixOps

import scala.io.Source

//Тестовый файл /home/sirius/Desktop/dockers
//Императивный код (программа может упасть в любом месте с Exception):
//Будет ли данный код работать стабильно ? Какие есть гарантии его надежности? Каким образом обработать нештатные ситуации ?
//Обычное решение - отладка, тестирование и Unit-тесты. Но они не дают полной гарантии.
def imperativeCopyFile: Unit =
  println("Введите путь к файлу:")
  val fileName = scala.io.StdIn.readLine()
  val data = Source.fromFile(fileName).getLines().mkString
  val prodData = "<---begin---" + data + "---end--->"
  println("Содержимое файла: ")
  println(s"$prodData")
  writeFile(fileName+"_copy", prodData)
  println(s"Создана копия файла ${fileName}_copy")

//Теперь давайте предположим, что мы хотим, чтобы наш код не падал с Exception, а вместо этого возвращал контролируемую ошибку типа ReadError/WriteError
//с описанием причины отказа во всех возможных случаях. Также в случае ошибки необходимо реализовать логику повторного запроса файла.
//При попытке императивной реализации нам придется оборачивать каждую строчку кода в try/catch, что приводит к потере читабельности и адскому болерплейту.
//При ФП подходе мы должны обернуть все наши value в типы ZIO, а также заменить императивную композицию - функциональной.
//Благодаря ф-ой композиции (map, flatMap) и типам (ZIO) компилятор может контролировать вашу программу через вывод типов, т.к. программа представляет
//непрерывный поток преобразований одного типа в другой через "чистые" ф-ии (ControlFlow => DataFlow)
//Таким образом  происходит самовалидация и мы можем дать экстремальные гарантии надежности кода и существенно облегчить рефакторинг и поддержку.

//Возможные классы ошибок
final case class ReadError(message:String)
final case class WriteError(message:String)

lazy val copyFile: ZIO[Console with Blocking, ReadError|WriteError, Unit] =
  for
    _        <- putStrLn("Введите путь к файлу:").!
    fileName <- getStrLn.orDie//orElseFail("Некорректый ввод")
    data     <- readFile(fileName).orElseFail(ReadError("Не удалось прочитать файл"))//orElseSucceed("Это содержимое по-умолчанию")
    prodData = "<---begin---" + data + "---end--->"
    _        <- putStrLn(s"Содержимое файла: ").!
    _        <- putStrLn(s"$prodData!").!
    _        <- writeFile(s"${fileName}_copy", prodData).orElseFail(WriteError("К сожалению не удалось создать copy файл"))
    _        <- putStrLn(s"Создана копия файла ${fileName}_copy")!
  yield ()

lazy val retryCopyFile:ZIO[Console with Blocking, Nothing, Unit] =
  copyFile.foldM(error=>putStrLn(s"Произошла ошибка: $error. Повторите ввод").! *> retryCopyFile, _ => ZIO.succeed(()))