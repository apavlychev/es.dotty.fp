package com.easysales.dotty.fp.app.zionomicon.emails

import zio.ZIO
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.random.{Random, nextIntBounded}
import zio.duration._

final case class Email(email:String, theme:String, body:String)

//Отправить уведомления по email
def sendEmail(email:Email):ZIO[Clock with Console with Random, Throwable, Boolean] =
  for
  random <- nextIntBounded(3).map(_ + 1)
  ret    <- random match
    case i if i < 3  => putStrLn(s"Отправка письма $email").delay(1.seconds) *>  ZIO.succeed(true)
    case _ => ZIO.fail(new Throwable("Не удалось соединиться с почтовым сервером"))
  yield ret