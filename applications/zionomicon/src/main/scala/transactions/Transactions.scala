package com.easysales.dotty.fp.app.zionomicon.transactions

import zio.ZIO

//Выполнение хуков без прерываний
def withHooks[R, E, A](a: A, before:A=>ZIO[R, E, A], zio:A=> ZIO[R, E, A], after:A=>ZIO[R, E, A] ):ZIO[R, E, A] =
  ZIO.uninterruptibleMask { restore =>
    for
      beforeValue <- before(a)
      procValue   <- restore(zio(beforeValue))
      afterValue  <- after(procValue)
    yield afterValue
  }