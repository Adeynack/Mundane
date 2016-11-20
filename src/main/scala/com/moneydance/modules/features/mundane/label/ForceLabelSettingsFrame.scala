package com.moneydance.modules.features.mundane.label

import scala.collection.JavaConverters._
import com.github.adeynack.scala.swing.{LCE, MigPanel}
import com.infinitekind.moneydance.model.TxnUtil
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Storage

import scala.swing.BorderPanel.Position
import scala.swing.FlowPanel.Alignment
import scala.swing.Swing._
import scala.swing.{Action, BorderPanel, Button, Dialog, FlowPanel, Frame, Label, ListView, Table}

class ForceLabelSettingsFrame(
  context: FeatureModuleContext,
  settings: Storage[ForceLabelSettings]
) extends Frame { frame =>

  override def closeOperation(): Unit = {
    dispose()
    super.closeOperation()
  }

  private val actionClose = Action("Close")(dispose())
  private var additionalLabel = Set.empty[String]

  contents = new MigPanel { rootPane =>

    columns
      .size("30%").fill.gap
      .grow.fill

    rows
      .shrink.fill.gap
      .grow.fill.gap
      .shrink.fill

    val actionNew = Action("New")(addConfiguration())
    val actionRename = Action("Rename")(renameConfiguration())
    val actionDelete = Action("Delete")(removeConfiguration())
    val actionRun = Action("Run")(runConfiguration())
    val actionRefreshLabels = Action("Refresh")(fillLabelList())
    val actionNewLabel = Action("New")(newAdditionalLabel())

    lay -- cc -- new Label("Configurations")
    val labelsTitlePanel = lay -- cc.wrap -- new FlowPanel(Alignment.Center)(
      new Label("Labels"),
      new Button(actionRefreshLabels),
      new Button(actionNewLabel)
    )

    val configurationList = lay -- cc -- new ListView[String]
    fillConfigurationList()

    val labelList = lay -- cc.wrap -- new ListView[String]
    fillLabelList()

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

    def fillLabelList() = labelList.listData = {
      val existing = TxnUtil.getListOfAllUsedTransactionTags(context.getCurrentAccountBook.getTransactionSet.getAllTxns).asScala.toSet
      val toDisplay = existing ++ additionalLabel
      toDisplay.toSeq.sorted
    }

    def newAdditionalLabel() = {
      // todo Dialog.showInput(rootPane., "Name of the new label", "Add a label")
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
