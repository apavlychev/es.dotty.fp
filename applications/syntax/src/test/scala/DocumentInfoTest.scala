package com.easysales.dotty.fp.app.syntax

import org.junit.Test
import org.junit.Assert._

import traits.{_}

class DocumentInfoTest :
  @Test def pretty_for_doc:Unit =
    val doc = new DocumentInfo("my doc") {}
    assertEquals("Doc: my doc", doc.pretty)

