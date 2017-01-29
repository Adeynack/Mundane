package com.moneydance.modules.features.mundane.label

import java.awt.event.{ActionEvent, ActionListener, WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, Dimension, FlowLayout}
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

import com.github.adeynack.scala.swing.mig.MigPanel
import com.github.adeynack.scala.swing._
import com.infinitekind.moneydance.model.TxnUtil
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Storage

import scala.collection.JavaConverters._

class ForceLabelSettingsFrame(
  context: FeatureModuleContext,
  settings: Storage[ForceLabelSettings]
) extends JFrame { frame =>

  //
  // GUI and fields
  //

  private var configurationMap: Map[String, Set[String]] = {
    settings.get.configurations.map {
      case ForceLabelConfiguration(name, labels) => name -> labels
    }.toMap
  }

  private var additionalLabels: Set[String] = configurationMap.values.flatten.toSet

  private val actionClose = SimpleAction("Close")(dispose)
  private val actionNew = SimpleAction("New")(addConfiguration)
  private val actionRename = SimpleAction("Rename")(renameConfiguration)
  private val actionDelete = SimpleAction("Delete")(removeConfiguration)
  private val actionRun = SimpleAction("Run")(runConfiguration)
  private val actionRefreshLabels = SimpleAction("Refresh")(fillLabelList)
  private val actionAddLabel = SimpleAction("Add")(newAdditionalLabel)

  private val content = new MigPanel {

    columns
      .size("30%").fill.gap
      .grow.fill

    rows
      .shrink.fill.gap
      .grow.fill.gap
      .shrink.fill

    lay a new JLabel("Configurations")

    val panLabelsTitle = lay at cc.wrap a new FlowPanel(FlowLayout.LEFT)(
      new JLabel("Labels"),
      new JButton(actionRefreshLabels)
    )

    val lstConfigurations = lay a new JList[String] {
      setSelectionMode(SINGLE_SELECTION)
      addListSelectionListener(LstConfigurationListSelectionListener)
    }

    val lstLabels = lay at cc.wrap a new CheckBoxList()

    lay at cc.spanX a new BorderPanel {
      lay at BorderLayout.WEST a new ButtonFlowPanel(FlowLayout.LEFT)(actionRun)
      lay at BorderLayout.CENTER a new ButtonFlowPanel(FlowLayout.CENTER)(actionNew,actionDelete,actionRename)
      lay at BorderLayout.EAST a new ButtonFlowPanel(FlowLayout.RIGHT)(actionClose)
    }

    // `panAddLabel` is added to the GUI in method `fillLabelList`.
    val panAddLabel = new FlowPanel(FlowLayout.LEFT)() {

      val txtNewLabel = new JTextField() {
        setPreferredSize(new Dimension(200, getPreferredSize.height))
        setAction(actionAddLabel)
      }
      this add txtNewLabel
      this add new JButton(actionAddLabel)

    }

  }

  //
  // Constructor
  //

  setDefaultCloseOperation(DISPOSE_ON_CLOSE)
  addWindowListener(ThisWindowListener)
  fillConfigurationList()
  setContentPane(content)
  setTitle(ForceLabel.name)
  setPreferredSize(new Dimension(1000, 600))
  pack()
  AwtUtil.centerWindow(this)

  //
  // Methods
  //

  private object ThisWindowListener extends WindowAdapter {
    override def windowClosed(e: WindowEvent): Unit = {
      saveSettings()
    }
  }

  private object LstConfigurationListSelectionListener extends ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = fillLabelList()
  }

  private def fillConfigurationList(): Unit = {
    val configurationNames = configurationMap.keys.toArray
    content.lstConfigurations.setListData(configurationNames)
    if (configurationNames.nonEmpty) {
      content.lstConfigurations.setSelectedIndex(0)
    } else {
      fillLabelList()
    }
  }

  private def fillLabelList(): Unit = {
    val selected = Option(content.lstConfigurations.getSelectedValue)
    actionRefreshLabels.setEnabled(selected.nonEmpty)
    selected.foreach { configName =>
      configurationMap.get(configName).foreach { selectedLabels =>
        val transactions = context.getCurrentAccountBook.getTransactionSet.getAllTxns
        val existing = TxnUtil.getListOfAllUsedTransactionTags(transactions).asScala.toSet
        val labelsForList = (existing ++ additionalLabels).toSeq.sorted(Ordering.String)
        val cbs = labelsForList.map { label =>
          val cb = new JCheckBox(label)
          cb.setSelected(selectedLabels.contains(label))
          cb.addActionListener(new ActionListener {
            override def actionPerformed(e: ActionEvent): Unit = {
              setLabelActiveInConfiguration(configName, label, cb.isSelected)
            }
          })
          cb
        }
        content.lstLabels.setListData(cbs.toArray)
        pack()
      }
    }
  }

  private def setLabelActiveInConfiguration(configName: String, label: String, active: Boolean) = {
    val actual = configurationMap(configName)
    if (active) {
      configurationMap += (configName -> (actual + label))
    } else {
      configurationMap += (configName -> (actual - label))
    }
  }

  private def newAdditionalLabel(): Unit = {
    val label = content.panAddLabel.txtNewLabel.getText()
    additionalLabels += label
    content.panAddLabel.txtNewLabel.setText("")
    Option(content.lstConfigurations.getSelectedValue).foreach(setLabelActiveInConfiguration(_, label, true))
    fillLabelList()
  }

  private def addConfiguration(): Unit = ???

  private def removeConfiguration(): Unit = ???

  private def renameConfiguration(): Unit = ???

  private def runConfiguration(): Unit = {
    // Before starting a run, save the current configuration (in case it changed)
    saveSettings()

    // todo: Perform the labelling run.
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

}
