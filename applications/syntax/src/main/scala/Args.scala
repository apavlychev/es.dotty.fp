package com.easysales.dotty.fp.app.syntax

//import org.rogach.scallop.{_}

enum Types (val descr:String):
  case Pets extends Types("create pets")
  case Docs extends Types("create docs")
  case Templates extends Types("generate templates")

//https://github.com/scallop/scallop
class Args(args: Array[String]): //extends ScallopConf(args):
  //lazy val process = opt[String](required = true, default = Some("Pets"))
  //verify()

  //def getProcess:Types=Types.valueOf(process.getOrElse("Pets"))
  def getProcess:Types=Types.valueOf(args.headOption.getOrElse("Pets"))