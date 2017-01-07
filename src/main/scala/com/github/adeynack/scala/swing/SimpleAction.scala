package com.github.adeynack.scala.swing

import java.awt.event.ActionEvent
import javax.swing.{AbstractAction, Action, Icon}

object SimpleAction {

  def apply(name: String)(action: () => Unit): SimpleAction = new AbstractAction(name) with SimpleAction {

    override def actionPerformed(e: ActionEvent): Unit = action()

    def apply(): Unit = action()

  }

  def apply(name: String, icon: Icon)(action: () => Unit): SimpleAction = new AbstractAction(name, icon) with SimpleAction {

    override def actionPerformed(e: ActionEvent): Unit = action()

    def apply(): Unit = action()

  }

}

trait SimpleAction extends Action {

  def apply()

}