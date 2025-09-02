package com.easysales.dotty.fp.app.zionomicon.models

//Персональная карточка
sealed trait PersonBase:
  def id: Int

final case class Person(id: Int, firstName: String, lastName: String) extends PersonBase
final case class NotValidatedPerson(id: Int, message: String)         extends PersonBase
final case class FailPerson(id: Int)                                  extends PersonBase
