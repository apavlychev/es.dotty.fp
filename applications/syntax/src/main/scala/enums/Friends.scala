package com.easysales.dotty.fp.app.syntax.enums

//https://dotty.epfl.ch/docs/reference/enums/enums.html

enum Friends:
  case Oly, Alex, Kolya

enum Pets(val owner:Friends):
  def pretty:String=s"O: $owner, V: ${ordinal}, L: $enumLabel"
  
  case Leya extends Pets(Friends.Oly)
  case Ozef extends Pets(Friends.Kolya)
  case Matilda extends Pets(Friends.Alex)