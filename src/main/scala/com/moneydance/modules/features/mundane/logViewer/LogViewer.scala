package com.moneydance.modules.features.mundane.logViewer

import java.awt.{BorderLayout, Dimension, Font}
import java.time.LocalDateTime
import javax.swing.text.DefaultCaret
import javax.swing.{JFrame, JTextArea}

import com.github.adeynack.scala.Utils.init
import com.github.adeynack.scala.swing.BorderPanel
import com.moneydance.modules.scalamd.{Logger, SingletonFrameSubFeature, SubFeatureContext}

object LogViewer extends SingletonFrameSubFeature[LogViewerFrame] {

  override def name: String = "Log Viewer"

  override protected def createFrame(context: SubFeatureContext): LogViewerFrame = new LogViewerFrame(context)

}

class LogViewerFrame(context: SubFeatureContext) extends JFrame {

  //
  // GUI
  //

  val content = new BorderPanel() {

    val txtConsole = lay at BorderLayout.CENTER aScrolled new JTextArea("") {
      setEditable(false)
      setFont(new Font("Courrier New", Font.BOLD, 11))
      init(getCaret.asInstanceOf[DefaultCaret]) { c =>
        c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE)
      }
    }

  }

  //
  // Constructor
  //

  context.addLogger(LogListener)

  setContentPane(content)
  setTitle(LogViewer.name)
  setPreferredSize(new Dimension(1200, 600))
  pack()

  //
  // Objects and Methods
  //

  private object LogListener extends Logger {

    private def addEntry(level: String, message: String, error: Option[Throwable]): Unit = {
      val date = LocalDateTime.now().toString
      val failure = error.map(e => s"\n$e").getOrElse("")
      content.txtConsole.append(s"$date [$level] $message$failure\n\n")
    }

    override def info(message: String): Unit = addEntry("INFO", message, None)

    override def error(message: String, error: Throwable): Unit = addEntry("ERROR", message, Option(error))

  }

}
