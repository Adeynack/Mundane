package com.github.adeynack.scala

object ToOption {

  implicit class SeqToOption[A](val underlying: Traversable[A]) extends AnyVal {

    def noneIfEmpty: Option[Traversable[A]] = if (underlying.isEmpty) None else Some(underlying)

  }

  implicit class StringToOption(val underlying: String) extends AnyVal {

    def noneIfEmpty: Option[String] = if (underlying.isEmpty) None else Some(underlying)

  }

}
