package com.moneydance.modules.features.mundane.label

import java.awt.Color

import com.github.adeynack.scala.swing.{LCE, MigPanel}
import com.infinitekind.moneydance.model.TxnUtil
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Storage
import net.miginfocom.layout.CC

import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.swing.BorderPanel.Position
import scala.swing.FlowPanel.Alignment
import scala.swing.ListView.IntervalMode
import scala.swing.Swing._
import scala.swing.event.{ButtonClicked, ListSelectionChanged}
import scala.swing.{Action, BorderPanel, BoxPanel, Button, CheckBox, Color, FlowPanel, Frame, Label, ListView, Orientation, TextField}

class ForceLabelSettingsFrame(
  context: FeatureModuleContext,
  settings: Storage[ForceLabelSettings]
) extends Frame { frame =>

  override def closeOperation(): Unit = {
    dispose()
    super.closeOperation()
  }

  private val actionClose = Action("Close")(dispose())

  private var additionalLabels = Set.empty[String]

  private var configurationMap: Map[String, Set[String]] = {
    settings.get.configurations.map {
      case ForceLabelConfiguration(name, labels) => name -> labels
    } toMap
  }

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

    val configurationList = lay -- cc -- new ListView[String] {
      selection.intervalMode = IntervalMode.Single
      listenTo(selection)
      reactions += {
        case ListSelectionChanged(_, _, _) =>
          fillLabelList()
      }
    }

    val labelList = lay -- cc.wrap -- new MigPanel {
      columns.align("left")
      background = Color.blue
    }

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

    fillConfigurationList()

    def fillConfigurationList(): Unit = {
      configurationList.listData = configurationMap.keys.toSeq
      if (configurationList.listData.nonEmpty) {
        configurationList.selectIndices(1)
      } else {
        fillLabelList()
      }
    }

    def fillLabelList(): Unit = {
      labelList.contents.clear()
      configurationList.selection.items.headOption.foreach { configName =>
        configurationMap.get(configName).foreach { selectedLabels =>
          val transactions = context.getCurrentAccountBook.getTransactionSet.getAllTxns
          val existing = TxnUtil.getListOfAllUsedTransactionTags(transactions).asScala.toSet
          val labelCheckboxes = (existing ++ additionalLabels).toSeq.sorted.map { label =>
            new CheckBox(label) {
              selected = selectedLabels.contains(label)
              reactions += {
                case ButtonClicked(_) =>
                  val actual = configurationMap(configName)
                  if (selected) {
                    configurationMap += (configName -> (actual + label))
                  } else {
                    configurationMap += (configName -> (actual - label))
                  }
                  saveSettings()
              }
            }
          }
          //          labelList.contents.appendAll(labelCheckboxes)
          //          labelList.contents.append(new FlowPanel(Alignment.Left)(
          labelCheckboxes.foreach(cb => labelList.add(cb, new CC().wrap))
          labelList.add(new FlowPanel(Alignment.Left)(
            new TextField() {
              this.preferredSize = (200, this.preferredSize.height)
            },
            new Button()
          ), new CC().wrap)
          pack()
        }
      }
    }

    def newAdditionalLabel(): Unit = {
      // todo Dialog.showInput(rootPane., "Name of the new label", "Add a label")
    }

    def addConfiguration(): Unit = ???

    def removeConfiguration(): Unit = ???

    def renameConfiguration(): Unit = ???

    def runConfiguration(): Unit = ???
  }

  private def saveSettings(): Unit = {
    val configurationSeq = configurationMap.map {
      case (name, labels) =>
        ForceLabelConfiguration(name, labels)
    }.toSeq
    settings.update(_.copy(
      configurations = configurationSeq
    ))
  }

  title = ForceLabel.name
  preferredSize = (1000, 600)
  pack()
  AwtUtil.centerWindow(this.peer)

}
