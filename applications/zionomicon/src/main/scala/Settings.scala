package com.easysales.dotty.fp.app.zionomicon

import java.net.URI
import java.util.Map.Entry

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}

object Settings:
  lazy val config = ConfigFactory.load()
  lazy val name = config.getString("client.info.owner.name")
  lazy val descr = config.getString("client.info.owner.description")
  lazy val age = config.getInt("client.info.team.avgAge")