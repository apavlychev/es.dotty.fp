package com.easysales.dotty.fp.app.zionomicon.emails

import zio.{IO, Random, ZIO, durationInt}
import zio.Duration.*
import com.easysales.dotty.fp.app.zionomicon.utils.ConsoleExt.*

final case class Email(email: String, theme: String, body: String)

//Отправить уведомления по email
def sendEmail(email: Email): IO[Throwable, Boolean] =
  for {
    random <- Random.nextIntBounded(3).map(_ + 1)
    ret    <- random match {
                case i if i < 3 => printLine(s"Отправка письма $email").delay(1.seconds) *> ZIO.succeed(true)
                case _          => ZIO.fail(new Throwable("Не удалось соединиться с почтовым сервером"))
              }
  } yield ret
