package com.easysales.dotty.fp.app.zionomicon.processes

import com.easysales.dotty.fp.app.zionomicon.emails.{Email,sendEmail}
import com.easysales.dotty.fp.app.zionomicon.models.Person
import com.easysales.dotty.fp.app.zionomicon.repositories.{createPerson, getPersonById, readFile, savePerson, writeFile}
import com.easysales.dotty.fp.app.zionomicon.transactions.{_}
import com.easysales.dotty.fp.app.zionomicon.validators.{SavedError, validatePerson}
import zio.{Ref, URIO, ZIO}
import zio.blocking.Blocking
import zio.clock.{Clock, currentDateTime}
import zio.console.{Console, putStrLn, putStrLnErr}
import zio.random.Random
import zio.duration._


//Массовое создание персональных карточек
final case class DbLost(message:String)
final case class NotValidated(message:String)

//penFile("config.json").catchAll(_ => IO.succeed(defaultConfig))
def createOrUpdatePerson(id:Int):ZIO[Console with Clock with Random, DbLost|NotValidated, Person]=
  for
    person         <- getPersonById(id).foldM({ case None => createPerson(id)
                                                 case Some(er) => ZIO.fail(DbLost(s"Проблема с соединением: $er"))},
                                               p=>ZIO.succeed(p.copy(id=id, firstName=s"${p.firstName}_U", lastName=s"${p.lastName}_U")))
    check          <- validatePerson(person).catchAll(er=>ZIO.fail(NotValidated(er.map(_.toString).mkString("<--",",","-->"))))
    procPerson     <- if check then (
                        for
                          savedPerson <- withHooks(person, p => putStrLn(s"Before обработки $p") *> ZIO.succeed(p),
                                                           savePerson(_),
                                                           p => putStrLn(s"After обработки $p") *> ZIO.succeed(p))
                        yield savedPerson
                      ).catchAll(er=>ZIO.fail(DbLost(s"Проблема с соединением: $er"))) else ZIO.fail(NotValidated("Неизвестная ошибка при валидации"))
  yield procPerson

//Счетчики ошибок 
final case class Counters(one:Int = 0, retry:Int = 0, notValid:Int = 0, fail:Int=0)

//Поток раз в 3 секунды сбрасывает статистику на диск и отправляет письмо при завершении
def loggingStats(ref:Ref[Counters]):URIO[Clock with Blocking with Console with Random, Unit]=
  (for
     counts <- ref.get
     time   <- currentDateTime <> putStrLnErr("Не удалось получить тек. время")
     _      <- writeFile(s"persons_stats", s"$time - $counts") <> putStrLnErr("Не удалось записать статистику")
   yield ()
   ).delay(3.seconds).forever.ensuring(ref.get.flatMap(c=>sendEmail(Email("support@aetp.ru", "Статистика создания персон", c.toString()))).retryN(3)
    <> putStrLnErr("Не удалось отправить сообщение на почтовый сервер") )//.disconnect


lazy val makeAllPersons: ZIO[Blocking with Console with Clock with Random, Nothing, Unit] =
  for
    _             <- putStrLn("Массовое создание персон")
    ref           <- Ref.make(Counters())
    fiber         <- loggingStats(ref).fork
    _             <- ZIO.foreachParN(20000)(1 to 100000)(id=>createOrUpdatePerson(id).flatMap(r=>ref.update(c=>c.copy(one = c.one+1))).catchAll {
                                                          case DbLost(message) => putStrLn(s"Повторная попытка создать персону с id $id: $message")
                                                                           *> createOrUpdatePerson(id).retryN(5).flatMap(r=>ref.update(c=>c.copy(retry= c.retry+1)))
                                                                          <> (putStrLnErr(s"Было сделано 5 попыток с id $id") *> ref.update(c=>c.copy(fail= c.fail+1)))
                                                          case NotValidated(message) => putStrLnErr(s"Не удалось создать персону: $message") 
                                                                                        *> ref.update(c=>c.copy(notValid=c.notValid+1))
                                                            })
    counts        <- ref.get
    _             <- putStrLn(s"Массовое создание персон: One ${counts.one}, Retry ${counts.retry}, NotValid ${counts.notValid}, Fail ${counts.fail}")
  yield () 