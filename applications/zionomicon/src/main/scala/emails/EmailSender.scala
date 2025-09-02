package com.easysales.dotty.fp.app.zionomicon.emails

import zio.ZIO
import zio.Clock
import zio.Console
import zio.Random
import zio.Duration
import zio.Duration.*
import com.easysales.dotty.fp.app.zionomicon.utils.ConsoleExt.*

final case class Email(email: String, theme: String, body: String)

//Отправить уведомления по email
def sendEmail(email: Email): ZIO[Any, Throwable, Boolean] = // Clock with Console with Random
  for
    random <- Random.nextIntBounded(3).map(_ + 1)
    ret    <- random match {
                case i if i < 3 => printLine(s"Отправка письма $email").delay(Duration.fromSeconds(1)) *> ZIO.succeed(true)
                case _          => ZIO.fail(new Throwable("Не удалось соединиться с почтовым сервером"))
              }
  yield ret
