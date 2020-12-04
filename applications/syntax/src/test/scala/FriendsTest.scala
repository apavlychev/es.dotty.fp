package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._
import enums.{_}

class FriendsTest :
  @Test def kolya_have_2_ordinal: Unit =
    assertEquals(2, Friends.Kolya.ordinal)

  @Test def leya_have_oly_owner: Unit =
    assertEquals(Friends.Oly, Pets.Leya.owner)

  @Test def alex_have_same_label: Unit =
    assertEquals("Alex", Friends.Alex.productPrefix)

  @Test def leya_display_str: Unit =
    assertEquals(s"O: Oly, V: 0, L: Leya", Pets.Leya.pretty)  
