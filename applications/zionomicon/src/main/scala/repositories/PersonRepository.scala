package com.easysales.dotty.fp.app.zionomicon.repositories

import com.easysales.dotty.fp.app.zionomicon.models.{Person}
import zio.{IO, Ref, UIO, URIO, ZIO}
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.random.{Random, nextInt, nextIntBounded}
import zio.duration._


//Получение персональной карточки по id
def getPersonById(id:Int):ZIO[Clock with Random, Option[Throwable], Person] = 
  for 
    random <- nextIntBounded(3).map(_ + 1) //ZIO.never //Clock.live.andTo()
    ret    <- random match
      case 1 => ZIO.succeed(Person(id, s"Alex", s"Pav")).delay(1.seconds)
      case 2 => ZIO.fail(None) //Не найдена в БД
      case _ => ZIO.fail(Option(new Throwable("Не удалось соединиться с БД")))
  yield ret

//Создание персональной карточки по id
def createPerson(id:Int):ZIO[Random, Nothing, Person] =
  for
    random <- nextIntBounded(10).map(_ + 1) 
    person    <- random match
      case 1 => ZIO.succeed(Person(id, s"", s"")) //Невалидная
      case _ => ZIO.succeed(Person(id, s"Новый", s"Новый"))
  yield person


//Сохранение (без транзакции)
def savePerson(person:Person):ZIO[Clock with Console with Random, Throwable, Person] = 
  for 
    _   <- putStrLn(s"Начинаем сохранять $person в БД")
    random <- nextIntBounded(3).map(_ + 1) 
    ret    <- random match
      case i if i < 3  => putStrLn(s"Идет сохранение $person в БД").delay(1.seconds) *> putStrLn(s"Завершаем сохранение $person в БД") *> ZIO.succeed(true)
      case _ => ZIO.fail(new Throwable("Не удалось соединиться с БД"))
  yield person

