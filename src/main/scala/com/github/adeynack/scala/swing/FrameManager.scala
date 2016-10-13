package com.github.adeynack.scala.swing

import scala.swing.Frame
import scala.swing.event.WindowClosed

class FrameManager(val builder: () => Frame) {

  private var frame: Option[Frame] = None

  def show(): Unit = this.synchronized {
    if (frame.isEmpty) {
      frame = Some {
        val f = builder()
        f.reactions += {
          case WindowClosed(_) => this.synchronized(frame = None)
        }
        f
      }
    }
    frame.foreach { f =>
      f.visible = true
      f.peer.toFront()
      f.peer.requestFocus()
    }
  }

  def close(): Unit = this.synchronized {
    frame.foreach { f =>
      f.visible = false
      f.dispose()
    }
    frame = None
  }

}
