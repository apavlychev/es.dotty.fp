package com.easysales.dotty.fp.app.syntax.tagless

trait Razor[F[_]]:
  def map[A, B](d:F[A])(a:A=>B):F[B]
  def flatMap[A, B](d:F[A])(a:A=>F[B]):F[B]
  def template[A,B](d:F[A])(r:F[B])(s:(A,B)=>String):F[String] =
    flatMap(d)(a=>map(r)(b=>s(a,b)))

given Razor[List] with
  override def map[A, B](d: List[A])(a: A => B): List[B] = d.map(a)
  override def flatMap[A, B](d: List[A])(a: A => List[B]): List[B] = d.flatMap(a)

given Razor[Option] with
  override def map[A, B](d: Option[A])(a: A => B): Option[B] = d.map(a)
  override def flatMap[A, B](d: Option[A])(a: A => Option[B]): Option[B] = d.flatMap(a)
  
extension [F[_],A,B](d:F[A]) def template(s:F[B])(r:(A,B)=>String)(using Razor[F]):F[String] =
  implicitly[Razor[F]].template(d)(s)(r)