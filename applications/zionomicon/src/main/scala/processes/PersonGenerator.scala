package com.easysales.dotty.fp.app.zionomicon.processes

import com.easysales.dotty.fp.app.zionomicon.emails.{Email, sendEmail}
import com.easysales.dotty.fp.app.zionomicon.models.{FailPerson, NotValidatedPerson, Person, PersonBase}
import com.easysales.dotty.fp.app.zionomicon.repositories.{createPerson, getPersonById, savePerson, writeFile}
import com.easysales.dotty.fp.app.zionomicon.transactions.*
import com.easysales.dotty.fp.app.zionomicon.validators.validatePerson
import zio.{Clock, Duration, IO, Ref, UIO, ZIO, durationInt}
import com.easysales.dotty.fp.app.zionomicon.utils.ConsoleExt.*
import com.easysales.dotty.fp.app.zionomicon.html.{
  Syntax,
  createFailPersonTable,
  createNotValidatedPersonTable,
  createPersonTable
}

//https://scastie.scala-lang.org/72JOdbiLSyKXaxpEyGaxOA
//type DefEnv = Console with Clock with Random

//Массовое создание персональных карточек:
//На входе: массив id-ов персон
//На выходе: html-отчеты
//P.S. в течение работы программы выполняется диагностика и результаты отправляются на почту

//Классы ошибок создания
final case class DbLost(message: String)
final case class NotValidated(message: String)

//Создаем/обновляем одну персону
def createOrUpdatePerson(id: Int): IO[DbLost | NotValidated, Person] =
  for {
    person     <- getPersonById(id).foldZIO(
                    {
                      case None      => createPerson(id)
                      case Some(err) => ZIO.fail(DbLost(s"Проблема с соединением: $err"))
                    },
                    p => ZIO.succeed(p.copy(id = id, firstName = s"${p.firstName}_U", lastName = s"${p.lastName}_U"))
                  )
    check      <-
      validatePerson(person).catchAll(er => ZIO.fail(NotValidated(er.map(_.toString).mkString("<--", ",", "-->"))))
    procPerson <- if check then
                    withHooks(
                      person,
                      p => printLine(s"Before обработки $p") *> ZIO.succeed(p),
                      savePerson,
                      p => printLine(s"After обработки $p") *> ZIO.succeed(p)
                    ).catchAll(er => ZIO.fail(DbLost(s"Проблема с соединением: $er")))
                  else ZIO.fail(NotValidated("Неизвестная ошибка при валидации"))
  } yield procPerson

//Обработка одной персоны
def makePerson(id: Int, ref: Ref[Counters]): UIO[PersonBase] =
  createOrUpdatePerson(id).flatMap(r => ref.update(c => c.copy(one = c.one + 1)) *> ZIO.succeed(r)).catchAll {
    case DbLost(message)       =>
      printLine(s"Повторная попытка создать персону с id $id: $message")
        *> createOrUpdatePerson(id)
          .retryN(5)
          .flatMap(r => ref.update(c => c.copy(retry = c.retry + 1)) *> ZIO.succeed(r))
        <> (printLineError(s"Было сделано 5 попыток с id $id") *> ref.update(c => c.copy(fail = c.fail + 1)) *> ZIO
          .succeed(FailPerson(id)))
    case NotValidated(message) =>
      printLineError(s"Не удалось создать персону: $message")
        *> ref.update(c => c.copy(notValid = c.notValid + 1)) *> ZIO.succeed(NotValidatedPerson(id, message))
  }

//Счетчики ошибок
final case class Counters(one: Int = 0, retry: Int = 0, notValid: Int = 0, fail: Int = 0)

//Поток раз в 3 секунды сбрасывает статистику на диск и отправляет письмо при завершении
def loggingStats(ref: Ref[Counters]): UIO[Unit] =
  (for {
    counts <- ref.get
    time   <- Clock.currentDateTime
    _      <- writeFile(s"persons_stats", s"$time - $counts") <> printLineError("Не удалось записать статистику")
  } yield ())
    .delay(Duration.fromSeconds(3))
    .forever
    .ensuring(
      ref.get
        .flatMap(c => sendEmail(Email("support@aetp.ru", "Статистика создания персон", c.toString())))
        .retryN(3)
        <> printLineError("Не удалось отправить сообщение на почтовый сервер")
    ) // .disconnect

def prepareReports: UIO[Unit] =
  for
    _ <- printLine("Подготовка отчетов")
    _ <- printLine("Идет подготовка отчетов...").delay(1.seconds).forever.fork
  yield ()

//Подготовка отчетов
//https://stackoverflow.com/questions/6372136/how-to-cast-each-element-in-scala-list
def makeReports(persons: Seq[PersonBase]): UIO[Unit] =
  ZIO.transplant { graft =>
    for
      _                  <- printLine("Начинаем создание отчетов")
      _                  <- graft(prepareReports).fork
      savedCaption        = "Сохраненные персоны"
      notValidatedCaption = "Некорректные персоны"
      failCaption         = "Несохраненные персоны"
      errorMessage        = "Не удалось сохранить файл отчета"
      successMessage      = "Создан файл отчета"
      savedFiber         <- createPersonTable(persons.collect { case p: Person => p })
                              .delay(6.seconds)
                              .flatMap(h =>
                                writeFile("Person.html", h.toString(savedCaption), false)
                                  *> printLine(s"$successMessage: $savedCaption")
                                  <> printLine(s"$errorMessage: $savedCaption")
                              )
                              .fork
      notValidatedFiber  <- createNotValidatedPersonTable(persons.collect { case p: NotValidatedPerson => p })
                              .delay(4.seconds)
                              .flatMap(h =>
                                writeFile("NotValidatedPerson.html", h.toString(notValidatedCaption), false)
                                  *> printLine(s"$successMessage: $notValidatedCaption")
                                  <> printLine(s"$errorMessage: $notValidatedCaption")
                              )
                              .fork
      failFiber          <- createFailPersonTable(persons.collect { case p: FailPerson => p })
                              .delay(3.seconds)
                              .flatMap(h =>
                                writeFile("FailPerson.html", h.toString(failCaption), false)
                                  *> printLine(s"$successMessage: $failCaption")
                                  <> printLine(s"$errorMessage: $failCaption")
                              )
                              .fork

      _ <- savedFiber.await
      _ <- notValidatedFiber.await
      _ <- failFiber.await
      _ <- printLine("Отчеты созданы")
    yield ()
  }

//Обработка 100 000 персон в 20 000 потоков
lazy val makeAllPersons: UIO[Unit] =
  for
    _           <- printLine("Массовое создание персон")
    ref         <- Ref.make(Counters())
    _           <- loggingStats(ref).fork
    persons     <- ZIO.foreachPar(1 to 100_000)(makePerson(_, ref)).withParallelism(20_000)
    reportFiber <- makeReports(persons).fork
    counts      <- ref.get
    _           <-
      printLine(
        s"Массовое создание персон: One ${counts.one}, Retry ${counts.retry}, NotValid ${counts.notValid}, Fail ${counts.fail}"
      )
    _           <- reportFiber.await
  yield ()
