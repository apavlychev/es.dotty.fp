package com.easysales.dotty.fp.app.zionomicon.processes

import com.easysales.dotty.fp.app.zionomicon.emails.{Email, sendEmail}
import com.easysales.dotty.fp.app.zionomicon.models.{FailPerson, NotValidatedPerson, Person, PersonBase}
import com.easysales.dotty.fp.app.zionomicon.repositories.{createPerson, getPersonById, readFile, savePerson, writeFile}
import com.easysales.dotty.fp.app.zionomicon.transactions._
import com.easysales.dotty.fp.app.zionomicon.validators.{SavedError, validatePerson}
import zio.{Ref, URIO, ZIO}
import zio.blocking.Blocking
import zio.clock.{Clock, currentDateTime}
import zio.console.{Console, putStrLn, putStrLnErr}
import zio.random.Random
import zio.duration._
import com.easysales.dotty.fp.app.zionomicon.html.{createPersonTable, Syntax, createNotValidatedPersonTable, createFailPersonTable}
import com.easysales.dotty.fp.app.zionomicon.repositories.{readFile, writeFile}


//Массовое создание персональных карточек:
//На входе: массив id-ов персон
//На выходе: html-отчеты
//P.S. в течение работы программы выполняется диагностика и результаты отправляются на почту

//Классы ошибок создания
final case class DbLost(message:String)
final case class NotValidated(message:String)

//Создаем/обновляем одну персону
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

//Обработка одной персоны
def makePerson(id:Int, ref:Ref[Counters]):ZIO[Blocking with Console with Clock with Random, Nothing, PersonBase]=
  createOrUpdatePerson(id).flatMap(r=>ref.update(c=>c.copy(one = c.one+1)) *> ZIO.succeed(r)).catchAll {
    case DbLost(message) => putStrLn(s"Повторная попытка создать персону с id $id: $message")
      *> createOrUpdatePerson(id).retryN(5).flatMap(r=>ref.update(c=>c.copy(retry= c.retry+1)) *> ZIO.succeed(r))
      <> (putStrLnErr(s"Было сделано 5 попыток с id $id") *> ref.update(c=>c.copy(fail= c.fail+1)) *> ZIO.succeed(FailPerson(id)))
    case NotValidated(message) => putStrLnErr(s"Не удалось создать персону: $message")
      *> ref.update(c=>c.copy(notValid=c.notValid+1)) *> ZIO.succeed(NotValidatedPerson(id,message))
  }

//Счетчики ошибок 
final case class Counters(one:Int = 0, retry:Int = 0, notValid:Int = 0, fail:Int=0)

//Поток раз в 3 секунды сбрасывает статистику на диск и отправляет письмо при завершении
def loggingStats(ref:Ref[Counters]):URIO[Clock with Blocking with Console with Random, Unit]=
  (for
    counts <- ref.get
    time   <- currentDateTime <> putStrLnErr("Не удалось получить тек. время")
    _      <- writeFile(s"persons_stats", s"$time - $counts") <> putStrLnErr("Не удалось записать статистику")
   yield ()).delay(3.seconds).forever.ensuring(ref.get.flatMap(c=>sendEmail(Email("support@aetp.ru", "Статистика создания персон", c.toString()))).retryN(3)
                                                               <> putStrLnErr("Не удалось отправить сообщение на почтовый сервер") )//.disconnect

//Подготовка отчетов
//https://stackoverflow.com/questions/6372136/how-to-cast-each-element-in-scala-list
def makeReports(persons:Seq[PersonBase]):ZIO[Console with Blocking, Nothing, Unit] =
  for 
    _                  <- putStrLn("Начинаем создание отчетов")
    savedFiber         <- createPersonTable(persons.collect { case p:Person => p} ).flatMap(h=>writeFile("Person.html",h.toString("Сохраненные персоны"), false)
                                                                                                      <>putStrLn("Не удалось сохранить файл отчета")).fork
    notValidatedFiber  <- createNotValidatedPersonTable(persons.collect { case p:NotValidatedPerson => p} ).flatMap(h=>writeFile("NotValidatedPerson.html",h.toString("Некорректные персоны"), false)
                                                                                                      <>putStrLn("Не удалось сохранить файл отчета")).fork
    failFiber          <- createFailPersonTable(persons.collect { case p:FailPerson => p} ).flatMap(h=>writeFile("FailPerson.html",h.toString("Несохраненные персоны"), false)
                                                                                                      <>putStrLn("Не удалось сохранить файл отчета")).fork
    
    _          <- savedFiber.await //join 
    _          <- notValidatedFiber.await 
    _          <- failFiber.await   
    _          <- putStrLn("Отчеты созданы")
  yield () 

//Обработка 100 000 персон в 20 000 потоков
lazy val makeAllPersons: ZIO[Blocking with Console with Clock with Random, Nothing, Unit] =
  for
    _             <- putStrLn("Массовое создание персон")
    ref           <- Ref.make(Counters())
    fiber         <- loggingStats(ref).fork
    persons       <- ZIO.foreachParN(20_000)(1 to 100_000)(makePerson(_, ref))
    _             <- makeReports(persons)
    counts        <- ref.get
    _             <- putStrLn(s"Массовое создание персон: One ${counts.one}, Retry ${counts.retry}, NotValid ${counts.notValid}, Fail ${counts.fail}")
  yield () 