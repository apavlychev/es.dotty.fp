package com.easysales.dotty.fp.app.zionomicon
//import org.rogach.scallop.{_}

enum Types (val descr:String):
  case HelloWorld extends Types("Пример Hello World!")
  case HandleErrors extends Types("Работа по Главе 2 и 4")
  case HandleFibers extends Types("Работа по Главам 6-9")

//https://github.com/scallop/scallop
class Args(args: List[String]):// extends ScallopConf(args):
  //lazy val process = opt[String](required = true, default = Some("HelloWorld"))
  //verify()

  //def getProcess:Types=Types.valueOf(process.getOrElse("HelloWorld"))
  def getProcess:Types=Types.valueOf(args.headOption.getOrElse("HelloWorld"))