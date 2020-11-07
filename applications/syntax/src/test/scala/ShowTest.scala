package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._
import types.{ given _, _}

//https://www.scalatest.org/getting_started_with_junit_4_in_scala
class ShowTest :
  @Test def equals_pretty_for_long: Unit =
    val long:Long= 10_100
    assertEquals("Pretty-Long:10100", long.pretty())
  
  @Test def equals_pretty_for_int: Unit =    
    val int:Int= 10_10
    assertEquals("Pretty-Int:1010", int.pretty())

  @Test def equals_pretty_for_str: Unit =
    var str = "test"
    assertEquals("Pretty-Str:test", str.pretty())
  
  @Test def equals_pretty_for_list: Unit =     
    val list =List("1", "2", "3")
    assertEquals("<--Pretty-Str:1,Pretty-Str:2,Pretty-Str:3-->", list.pretty())

