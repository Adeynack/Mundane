package com.github.adeynack.scala

import java.awt.event.{ActionEvent, ActionListener}

object Utils {

  def init[T](o: T)(i: (T) => Unit): T = {
    i(o)
    o
  }

  implicit def func2runnable(f: => Unit): Runnable = new Runnable {
    override def run(): Unit = f
  }

  implicit def func2actionListener(f: => Unit): ActionListener = new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = f
  }

  def typedToString(o: Any, v: Any): String = s"${o.getClass.getSimpleName} $v"

}
