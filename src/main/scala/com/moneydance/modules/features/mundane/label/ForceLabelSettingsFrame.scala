package com.moneydance.modules.features.mundane.label

import com.github.adeynack.scala.swing.{LCE, MigPanel}
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Storage
import net.miginfocom.swing.MigLayout

import scala.swing.BorderPanel.Position
import scala.swing.FlowPanel.Alignment
import scala.swing.Swing._
import scala.swing.{Action, BorderPanel, Button, FlowPanel, Frame, ListView, Table}

class ForceLabelSettingsFrame(
  settings: Storage[ForceLabelSettings]
) extends Frame {

  override def closeOperation(): Unit = {
    dispose()
    super.closeOperation()
  }

  private val actionClose = Action("Close")(dispose())

  contents = new MigPanel {

//    constraints.debug

    columns
      .size("30%").fill.gap
      .grow.fill

    rows
      .grow.fill.gap
      .shrink

    val actionNew = Action("New")(addConfiguration())
    val actionRename = Action("Rename")(renameConfiguration())
    val actionDelete = Action("Delete")(removeConfiguration())
    val actionRun = Action("Run")(runConfiguration())

    val configurationList = lay -- cc -- new ListView[String]
    fillConfigurationList()

    val labelList = lay -- cc.wrap -- new Table

    lay -- cc.spanX -- new BorderPanel with LCE {
      lay -- Position.West -- new FlowPanel(Alignment.Left)(
        new Button(actionRun)
      )
      lay -- Position.Center -- new FlowPanel(Alignment.Center)(
        new Button(actionNew),
        new Button(actionDelete),
        new Button(actionRename)
      )
      lay -- Position.East -- new FlowPanel(Alignment.Right)(
        new Button(actionClose)
      )
    }

    def fillConfigurationList(): Unit = {
      configurationList.listData = settings.get.configurations.map(_.name)
    }

    def addConfiguration(): Unit = ???

    def removeConfiguration(): Unit = ???

    def renameConfiguration(): Unit = ???

    def runConfiguration(): Unit = ???
  }

  title = ForceLabel.name
  preferredSize = (1000, 600)
  pack()
  AwtUtil.centerWindow(this.peer)

}
