package com.easysales.dotty.fp.app.syntax.traits

trait DocumentInfo (descr: String):
  def pretty:String = s"Doc: $descr"
