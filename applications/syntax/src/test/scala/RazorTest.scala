package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._

import tagless.{given, _}

class RazorTest:
  @Test def generate_list_template: Unit =
    val template = List("S1","S2").template(List(1,2))((a,b)=>s"$a-$b")
    assertEquals(List("S1-1","S1-2","S2-1","S2-2"), template)

  @Test def generate_option_template: Unit =
    val template = Option("S1").template(Option(1))((a,b)=>s"$a-$b")
    assertEquals(Option("S1-1"), template)
