package com.moneydance.modules.features.mundane.logViewer

import java.awt.{BorderLayout, Dimension, Font}
import java.time.LocalDateTime
import javax.swing.text.DefaultCaret
import javax.swing.{JFrame, JTextArea}

import com.github.adeynack.scala.Utils
import com.github.adeynack.scala.Utils.init
import com.github.adeynack.scala.swing.BorderPanel
import com.moneydance.modules.features.mundane.logViewer.LogViewer.LogViewerSettings
import com.moneydance.modules.scalamd._
import play.api.libs.json.Json

object LogViewer extends SingletonFrameSubFeature[LogViewerFrame] {

  override def name: String = "Log Viewer"

  override protected def createFrame(context: SubFeatureContext): LogViewerFrame =
    new LogViewerFrame(context, getStorage(context))

  override def initialize(context: SubFeatureContext): Unit = {
    val settings = getStorage(context).get
    if (settings.openViewerOnStartup) {
      invoke(context)
    }
  }

  case class LogViewerSettings(
    openViewerOnStartup: Boolean = true
  ) {
    override def toString: String = Utils.typedToString(this, Json.toJson(this))
  }

  implicit val logViewerSettingsFormat = Json.format[LogViewerSettings]

  private def getStorage(context: SubFeatureContext) =
    context.getStorage[LogViewerSettings]("LogViewer", LogViewerSettings())

}

class LogViewerFrame(context: SubFeatureContext, settings: Storage[LogViewerSettings]) extends JFrame {

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
