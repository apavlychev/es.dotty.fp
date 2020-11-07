package com.easysales.dotty.fp.app.syntax.tupled

//https://dotty.epfl.ch/docs/reference/other-new-features/parameter-untupling.html

def listTupled[A, B](ls:List[(A, B)])(action:(A, B)=>Unit):Unit =
  ls.foreach { (a,b)=>action(a,b) }