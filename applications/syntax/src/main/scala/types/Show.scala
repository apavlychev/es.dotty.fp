package com.easysales.dotty.fp.app.syntax.types
//https://dotty.epfl.ch/docs/reference/contextual/type-classes.html

trait Show[T]:
  def pretty(a:T):String

object Show:
  def apply[T](using m: Show[T]) = m

given Show[String] with
  override def pretty(a: String): String = s"Pretty-Str:$a"

given Show[Int] with 
  override def pretty(a: Int): String = s"Pretty-Int:$a"

given Show[Long] with 
  override def pretty(a: Long): String = s"Pretty-Long:$a"

//https://dotty.epfl.ch/docs/reference/contextual/givens.html
given [T](using Show[T]): Show[List[T]] with
  override def pretty(a: List[T]): String = a.map(Show[T].pretty(_)).mkString("<--",",","-->")

//https://dotty.epfl.ch/docs/reference/contextual/using-clauses.html
extension [T:Show](s:T) def pretty():String=Show[T].pretty(s) //summon//implicitly

//extension [A:Show](s:List[A]) def listPretty():String= s. //s.mkString() //Show[A].pretty(s) //summon//implicitly

//extension [A](c:Test[?]) def display2(a:A):String=s"Dis2_2${a}"  