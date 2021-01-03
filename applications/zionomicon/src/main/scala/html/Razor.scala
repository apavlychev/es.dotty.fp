package com.easysales.dotty.fp.app.zionomicon.html

//https://www.youtube.com/watch?v=8-b2AoctkiY&list=PLmtsMNDRU0BwsVUbhsH2HMqDMPNhQ0HPc&index=4
object Syntax:
  opaque type Html = String

  object Html:
    def apply(str: String):Html = str
    lazy val none:Html="<none>"

    extension (value:Html) 
      def ^(other: Html): Html = s"<td>$value</td><td>$other</td>"

      def &(other: Html): Html = s"<th>$value</th><th>$other</th>"

      def ||(other: Html): Html = if value == Html.none then s"$other" else s"<tr>$value</tr><tr>$other</tr>"

      def +(other: Html): Html = s"$value$other"

      def unary_~ : Html = s"<tr>${value}</tr>"

      def toString(caption: String): String = s"<!DOCTYPE HTML>" +
        "<html>" +
        "<head>" +
        "<meta charset=\"utf-8\">" +
        s"<title>$caption</title>" +
        "</head>" +
        "<body>" +
        "<table>" +
        s"<caption>$caption</caption>" +
        value +
        "</table>" +
        "</body>" +
        "</html>"
    //}

////https://alvinalexander.com/scala/how-to-use-match-case-expression-isinstanceof-types-scala/
//https://docs.scala-lang.org/ru/tour/pattern-matching.html
//http://htmlbook.ru/html/table
//https://scastie.scala-lang.org/OhSirius/NowhgDADQKGXHdBIpEekxQ/53
///Пример с операторами
//Table()|"Scala"|"Java"||"Kotlin"|"QBasic"
//<table><tr><td>Scala</td><td>Java</td></tr><tr><td>
//~"Scala"*"Java"+"Kotlin"*"QBasic"
//<table><tr><td>Scala</td><td>Java</td></tr><tr><td>Kotlin</td><td>QBasic</td></tr></table>

//class HtmlOpt(val value: String):
//  def ^(other: String) = s"<td>${value}</td><td>${other}</td>"
//  def ^^(other: String) = s"<th>${value}</th><th>${other}</th>"
//  def ||(other: String) = s"<tr>${value}</tr><tr>${other}</tr>"
//  def unary_~ = s"<table>${value}</table>"
//  //def withTable = s"<table>${value}</table>"


//object HtmlOpt:
//  def apply(value: String) = new HtmlOpt(value)
//  def unapply(value: String) = value.trim.contains("table")
//  implicit def str2Opt(value: String) = HtmlOpt(value);


//object Main {
//  import HtmlOpt._
//  def main(args: Array[String]): Unit = {
//    //println("Test")
//    //val test = ~("Scala"*("Java"^"Kotlin")*"QBasic")
//    val table = ~("Scala" ^ "Java" || "Kotlin" ^ "QBasic")
//    table match {
//      case HtmlOpt() => println(s"${table}")
//      case _         => println(s"Не работает")
//    }
//
//  }
//}