package com.easysales.dotty.fp.app.zionomicon.html

import zio.ZIO
import zio.UIO
import com.easysales.dotty.fp.app.zionomicon.models.{FailPerson, NotValidatedPerson, Person}
import com.easysales.dotty.fp.app.zionomicon.html.Syntax._

lazy val maxRows = 10_000

def createPersonTable(persons: Seq[Person]): UIO[Html] =
  for
    headers <- ZIO.succeed(Html("id") & Html("firstName") & Html("lastName"))
    items   <- ZIO.foldLeft(persons.take(maxRows))(Html.none)((s, p) =>
                 ZIO.succeed(s || Html(p.id.toString) ^ Html(p.firstName) ^ Html(p.lastName))
               )
    res      = ~headers + items
  yield res

def createNotValidatedPersonTable(persons: Seq[NotValidatedPerson]): UIO[Html] =
  for
    headers <- ZIO.succeed(Html("id") & Html("message"))
    items   <-
      ZIO.foldLeft(persons.take(maxRows))(Html.none)((s, p) => ZIO.succeed(s || Html(p.id.toString) ^ Html(p.message)))
    res      = ~headers + items
  yield res

def createFailPersonTable(persons: Seq[FailPerson]): UIO[Html] =
  for
    headers <- ZIO.succeed(Html("id") & Html("error"))
    items   <- ZIO.foldLeft(persons.take(maxRows))(Html.none)((s, p) =>
                 ZIO.succeed(s || Html(p.id.toString) ^ Html("Не удалось создать"))
               )
    res      = ~headers + items
  yield res
