package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._

import tupled.{_}

class TupledTest :
  @Test def check_untupled_foreach: Unit =
    listTupled(List((1,"1"),(2, "2")))((a,b)=>println(s"$a-$b"))
 
