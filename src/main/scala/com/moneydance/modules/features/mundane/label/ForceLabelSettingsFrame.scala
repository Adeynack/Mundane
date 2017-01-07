package com.moneydance.modules.features.mundane.label

import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, Dimension, FlowLayout}
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

import com.github.adeynack.scala.swing.SimpleAction
import com.infinitekind.moneydance.model.TxnUtil
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Storage
import net.miginfocom.layout.{AC, CC, LC}
import net.miginfocom.swing.MigLayout

import scala.collection.JavaConverters._

class ForceLabelSettingsFrame(
  context: FeatureModuleContext,
  settings: Storage[ForceLabelSettings]
) extends JFrame { frame =>

  setDefaultCloseOperation(DISPOSE_ON_CLOSE)

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

  private val content = new JPanel(new MigLayout(
    new LC(),
    new AC()
      .size("30%").fill.gap
      .grow.fill,
    new AC()
      .shrink.fill.gap
      .grow.fill.gap
      .shrink.fill
  ))

  content.add(new JLabel("Configurations"))

  private val panLabelsTitle = new JPanel(new FlowLayout(FlowLayout.LEFT))
  panLabelsTitle.add(new JLabel("Labels"))
  panLabelsTitle.add(new JButton(actionRefreshLabels))
  content.add(panLabelsTitle, new CC().wrap)

  private val lstConfigurations = new JList[String]
  lstConfigurations.setSelectionMode(SINGLE_SELECTION)
  lstConfigurations.addListSelectionListener(new ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = fillLabelList()
  })
  content.add(new JScrollPane(lstConfigurations))

  private val panLabelsList = new JPanel(new MigLayout(
    new LC(),
    new AC().align("left"),
    new AC()
  ))
  content.add(new JScrollPane(panLabelsList), new CC().wrap)

  content.add(new JPanel(new BorderLayout()) {

    add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {
      add(new JButton(actionRun))
    }, BorderLayout.WEST)

    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {
      add(new JButton(actionNew))
      add(new JButton(actionDelete))
      add(new JButton(actionRename))
    }, BorderLayout.CENTER)

    add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
      add(new JButton(actionClose))
    }, BorderLayout.EAST)

  }, new CC().spanX)

  // `panAddLabel` is added to the GUI in method `fillLabelList`.
  private val panAddLabel = new JPanel(new FlowLayout(FlowLayout.LEFT))
  private val txtNewLabel = new JTextField()
  txtNewLabel.setPreferredSize(new Dimension(200, txtNewLabel.getPreferredSize.height))
  txtNewLabel.setAction(actionAddLabel)
  panAddLabel.add(txtNewLabel)
  panAddLabel.add(new JButton(actionAddLabel))

  fillConfigurationList()

  setContentPane(content)

  setTitle(ForceLabel.name)
  setPreferredSize(new Dimension(1000, 600))
  pack()
  AwtUtil.centerWindow(this)

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private def fillConfigurationList(): Unit = {
    val configurationNames = configurationMap.keys.toArray
    lstConfigurations.setListData(configurationNames)
    if (configurationNames.nonEmpty) {
      lstConfigurations.setSelectedIndex(0)
    } else {
      fillLabelList()
    }
  }

  private def fillLabelList(): Unit = {
    panLabelsList.removeAll()
    val selected = Option(lstConfigurations.getSelectedValue)
    actionRefreshLabels.setEnabled(selected.nonEmpty)
    selected.foreach { configName =>
      configurationMap.get(configName).foreach { selectedLabels =>
        val transactions = context.getCurrentAccountBook.getTransactionSet.getAllTxns
        val existing = TxnUtil.getListOfAllUsedTransactionTags(transactions).asScala.toSet
        (existing ++ additionalLabels).toSeq.sorted(Ordering.String).foreach { label =>
          val cb = new JCheckBox(label)
          cb.setSelected(selectedLabels.contains(label))
          cb.addActionListener(new ActionListener {
            override def actionPerformed(e: ActionEvent): Unit = {
              setLabelActiveInConfiguration(configName, label, cb.isSelected)
            }
          })
          panLabelsList.add(cb, new CC().wrap)
        }
        panLabelsList.add(panAddLabel, new CC().wrap)
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
    saveSettings()
  }

  private def newAdditionalLabel(): Unit = {
    val label = txtNewLabel.getText()
    additionalLabels += label
    txtNewLabel.setText("")
    Option(lstConfigurations.getSelectedValue).foreach(setLabelActiveInConfiguration(_, label, true))
    fillLabelList()
  }

  private def addConfiguration(): Unit = ???

  private def removeConfiguration(): Unit = ???

  private def renameConfiguration(): Unit = ???

  private def runConfiguration(): Unit = ???

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
