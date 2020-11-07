package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._

import extensions.{_}

class CollectionExtTest:
  @Test def make_list:Unit =
    List("1", "2", "3").forList(x=>assert(x!=null))
