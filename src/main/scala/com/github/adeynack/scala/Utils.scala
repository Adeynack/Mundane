package com.github.adeynack.scala

object Utils {

  def init[T](o: T, i: (T) => Unit): T = {
    i(o)
    o
  }

}
