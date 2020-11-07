package com.easysales.dotty.fp.app.syntax.extensions

//https://dotty.epfl.ch/docs/reference/contextual/extension-methods.html

extension [T](ls:List[T]) def forList(op:T=>Unit):Unit =
  ls.foreach(op)