package com.easysales.dotty.fp.app.syntax
//import org.rogach.scallop.ScallopConf.{_, given _}
//import org.rogach.scallop.ScallopConf._

import types.given
import types.{_}
import extensions.{_}
import enums.{_}
import tupled.{_}
import traits.{_}
//import tagless.{given _, _}
import tagless.given
import tagless.{_}

object Main :

  def main(args: Array[String]): Unit =
    val conf = Args(args)
    println( s"Запуск приложения Syntax - name: ${Settings.name}, descr: ${Settings.descr}, age: ${Settings.age}")
    println(s"${conf.getProcess.descr}")
    conf.getProcess match {
      case Types.Pets =>
        createPets
      case Types.Docs =>
        createDocuments
      case Types.Templates =>
        generateTemplates
      case null => println("Не найден сценарий")
    }
    println("Завершение приложения Syntax")
  
  
  def createPets =
    val list = List(Pets.Leya, Pets.Ozef, Pets.Matilda)
    val owners = list.map(_.owner)
    val ids = list.map(_.ordinal)
    list.forList(println(_))
    owners.forList(println(_))
    ids.forList(println(_))

  def createDocuments =
    val list = List(Pets.Leya, Pets.Ozef, Pets.Matilda)
    list.map(x=>new DocumentInfo(x.productPrefix) {}).foreach(x=>println(x.pretty))
 
  def generateTemplates =
    val list = List(Pets.Leya, Pets.Ozef, Pets.Matilda).map(_.productPrefix).template(List(Friends.Oly, Friends.Alex, Friends.Kolya).map(_.productPrefix))((a,b)=>s"$a-$b")
    //listTupled(List((1,"1"),(2, "2")))((a,b)=>println(s"$a-$b"))
    println(list.mkString(","))
    


