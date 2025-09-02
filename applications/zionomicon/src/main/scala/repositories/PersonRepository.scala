package com.easysales.dotty.fp.app.zionomicon.repositories

import com.easysales.dotty.fp.app.zionomicon.models.Person
import zio.{IO, ZIO, durationInt}
import zio.Console
import zio.Console.*
import zio.Random
import zio.Duration

//Получение персональной карточки по id
def getPersonById(id: Int): IO[Option[Throwable], Person] =
  for {
    random <- Random.nextIntBounded(3).map(_ + 1)
    ret    <- random match
                case 1 => ZIO.succeed(Person(id, s"Alex", s"Pav")).delay(1.seconds)
                case 2 => ZIO.fail(None) // Не найдена в БД
                case _ => ZIO.fail(Option(new Throwable("Не удалось соединиться с БД")))
  } yield ret

//Создание персональной карточки по id
def createPerson(id: Int): IO[Nothing, Person] =
  for {
    random <- Random.nextIntBounded(10).map(_ + 1)
    person <- random match
                case 1 => ZIO.succeed(Person(id, s"", s"")) // Невалидная
                case _ => ZIO.succeed(Person(id, s"Новый", s"Новый"))
  } yield person

//Сохранение (без транзакции)
def savePerson(person: Person): IO[Throwable, Person] =
  for {
    _      <- printLine(s"Начинаем сохранять $person в БД")
    random <- Random.nextIntBounded(3).map(_ + 1)
    ret    <- random match
                case i if i < 3 =>
                  printLine(s"Идет сохранение $person в БД").delay(1.seconds) *> printLine(
                    s"Завершаем сохранение $person в БД"
                  ) *> ZIO.succeed(true)
                case _          => ZIO.fail(new Throwable("Не удалось соединиться с БД"))
  } yield person
