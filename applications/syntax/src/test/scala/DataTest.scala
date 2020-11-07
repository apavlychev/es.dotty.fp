package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._

import creators.{_}

class DataTest:
  @Test def create_data_success:Unit =
    DataInfo(1)

  @Test def equals_pretty:Unit =
    assertEquals("Data: 2", DataInfo(2).pretty)