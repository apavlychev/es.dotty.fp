package com.easysales.dotty.fp.app.zionomicon.validators

import com.easysales.dotty.fp.app.zionomicon.models.Person
import zio.{IO, Ref, ZIO}

//Ошибки при создании
final case class SavedError(message: String)

//Валидация
//https://github.com/denisshevchenko/breadu.info/blob/master/src/lib/BreadU/Tools/Validators.hs
def validatePerson(person: Person): IO[::[SavedError], Boolean] =
  for {
    ref    <- Ref.make(List[SavedError]())
    _      <- (if person.firstName == null || person.firstName.isEmpty then
                 ref.update(s => SavedError("Не определено имя") :: s)
               else ZIO.none) <*> // <&>
                (if person.lastName == null || person.lastName.isEmpty then
                   ref.update(s => SavedError("Не определена фамилия") :: s)
                 else ZIO.none)
    errors <- ref.get
    ret    <- if errors.isEmpty then ZIO.succeed(true) else ZIO.fail(::(errors.head, errors.tail))
  } yield ret
