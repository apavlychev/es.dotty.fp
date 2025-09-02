package com.easysales.dotty.fp.app.zionomicon.repositories

import com.easysales.dotty.fp.app.zionomicon.models.Person
import zio.{IO, Ref, UIO, URIO, ZIO}

//import scala.concurrent.duration.DurationInt
//import zio.clock.Clock
import zio.Clock
import zio.Console
import zio.Console.*
import zio.Random
import zio.Random.*
//import zio.random.{Random, nextInt, nextIntBounded}
import zio.Duration
import zio.Duration.*
//import zio.Duration.DurationSyntax.*

//Получение персональной карточки по id
def getPersonById(id: Int): ZIO[Any, Option[Throwable], Person] =
  for {
    random <- Random.nextIntBounded(3).map(_ + 1) // ZIO.never //Clock.live.andTo()
    ret    <- random match
                case 1 => ZIO.succeed(Person(id, s"Alex", s"Pav")).delay(Duration.fromSeconds(1))
                case 2 => ZIO.fail(None) // Не найдена в БД
                case _ => ZIO.fail(Option(new Throwable("Не удалось соединиться с БД")))
  } yield ret

//Создание персональной карточки по id
def createPerson(id: Int): ZIO[Any, Nothing, Person] =
  for {
    random <- Random.nextIntBounded(10).map(_ + 1)
    person <- random match
                case 1 => ZIO.succeed(Person(id, s"", s"")) // Невалидная
                case _ => ZIO.succeed(Person(id, s"Новый", s"Новый"))
  } yield person

//Сохранение (без транзакции)
def savePerson(person: Person): ZIO[Any, Throwable, Person] = // Clock with Console with Random
  for {
    _      <- printLine(s"Начинаем сохранять $person в БД")
    random <- Random.nextIntBounded(3).map(_ + 1)
    ret    <- random match
                case i if i < 3 =>
                  printLine(s"Идет сохранение $person в БД").delay(Duration.fromSeconds(1)) *> printLine(
                    s"Завершаем сохранение $person в БД"
                  ) *> ZIO.succeed(true)
                case _          => ZIO.fail(new Throwable("Не удалось соединиться с БД"))
  } yield person
