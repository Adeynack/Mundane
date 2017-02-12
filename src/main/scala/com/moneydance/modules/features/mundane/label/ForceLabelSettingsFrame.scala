package com.moneydance.modules.features.mundane.label

import com.github.adeynack.scala.Utils._

import com.moneydance.modules.scalamd.Extensions._
import java.awt.event.{ActionEvent, ActionListener, WindowAdapter, WindowEvent}
import java.awt.{Dimension, FlowLayout}
import java.time.LocalDate
import java.util.Calendar
import javax.swing.JOptionPane.INFORMATION_MESSAGE
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing.event.{ChangeEvent, ChangeListener, ListSelectionEvent, ListSelectionListener}
import javax.swing.{JList, _}

import com.github.adeynack.scala.swing._
import com.github.adeynack.scala.swing.mig.MigPanel
import com.infinitekind.moneydance.model.TxnUtil
import com.infinitekind.util.{CustomDateFormat, DateUtil}
import com.moneydance.apps.md.view.gui.MoneydanceGUI
import com.moneydance.awt.{AwtUtil, DateField, DatePicker, JDateField}
import com.moneydance.modules.features.mundane.label.ForceLabel.{ForceLabelConfiguration, ForceLabelSettings}
import com.moneydance.modules.scalamd.{Storage, SubFeatureContext}

import scala.collection.JavaConverters._
import scala.collection.TraversableLike
import scala.collection.generic.CanBuildFrom

class ForceLabelSettingsFrame(
  context: SubFeatureContext,
  settings: Storage[ForceLabelSettings]
) extends JFrame { frame =>

  //
  // GUI and fields
  //

  private var configurationMap: Map[String, ForceLabelConfiguration] = {
    settings.get.configurations.map(c => c.name -> c).toMap
  }

  private var additionalLabels: Set[String] = configurationMap.values.flatMap(_.labels).toSet

  private val actionClose = SimpleAction("Close")(dispose)
  private val actionNew = SimpleAction("New")(addConfiguration)
  private val actionRename = SimpleAction("Rename")(renameConfiguration)
  private val actionDelete = SimpleAction("Delete")(removeConfiguration)
  private val actionRun = SimpleAction("Run")(runConfiguration)
  private val actionRefreshLabels = SimpleAction("Refresh")(configToControls)
  private val actionAddLabel = SimpleAction("Add")(newAdditionalLabel)

  private val lblConfigurations = new JLabel("Configurations")
  private val lstConfigurations = new JList[String] {
    setSelectionMode(SINGLE_SELECTION)
    addListSelectionListener(LstConfigurationListSelectionListener)
  }
  private val lblName = new JLabel("Name")
  private val txtConfigName = new JTextField() {
    setEditable(false)
  }
  private val lblFrom = new JLabel("From")
  private val chkFrom = new JCheckBox() {
    addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        context.info(s"Setting datFrom visibility to `$isSelected`")
        datFrom.setVisible(isSelected)
        // todo: Why does that does not make them appear?!
      }
    })
  }
  private val datFrom = new JDateField(new CustomDateFormat("yyyy.MM.dd"))
  private val lblTo = new JLabel("To")
  private val chkTo = new JCheckBox() {
    addActionListener(new ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        context.info(s"Setting datTo visibility to `$isSelected`")
        datTo.setVisible(isSelected)
        // todo: Why does that does not make them appear?!
      }
    })
  }
  private val datTo = new JDateField(new CustomDateFormat("yyyy.MM.dd"))
  private val lblLabels = new JLabel("Labels")
  private val txtNewLabel = new JTextField() {
    setPreferredSize(new Dimension(200, getPreferredSize.getHeight.toInt))
  }
  private val btnAddNewLabel = new JButton(actionAddLabel)
  private val lstLabels = new CheckBoxList()
  private val pnlConfigurationButtons = new ButtonFlowPanel(FlowLayout.LEFT)(actionRun, actionNew, actionDelete)
  private val panFrameButtons = new ButtonFlowPanel(FlowLayout.RIGHT)(actionClose)

  private val content = new MigPanel {

    //    layoutConstraints.debug

    columns
      .size("30%").fill.gap
      .fill.gap
      .grow.fill

    rows
      .shrink.fill.gap //            | Configurations        | Name    | txtConfigName                    |
      .gap("0px") //                 | lstConfiguration      | From    | [ chkFrom | dateFrom ]           |
      .gap("0px") //                 | .vs2                  | To      | [ chkTo | dateTo ]               |
      .gap("0px") //                 | .vs3                  | Labels  | [ txtNewLabel | btnAddNewLabel ] |
      .grow.shrink.fill.gap //       | .vs4                  | lstLabels                                  |
      .shrink.fill //                | [ btnRun,New,Delete ] |                               [ btnClose ] |

    add(lblConfigurations)
    add(lblName)
    add(txtConfigName, cc.wrap)

    add(new JScrollPane(lstConfigurations), cc.spanY(4).grow)
    add(lblFrom)
    add(new FlowPanel(FlowLayout.LEFT)(chkFrom, datFrom), cc.wrap)

    add(lblTo)
    add(new FlowPanel(FlowLayout.LEFT)(chkTo, datTo), cc.wrap)

    add(lblLabels)
    add(new FlowPanel(FlowLayout.LEFT)(txtNewLabel, btnAddNewLabel), cc.wrap)

    add(new JScrollPane(lstLabels), cc.spanX(2).wrap)

    add(pnlConfigurationButtons)
    add(panFrameButtons, cc.spanX(2).alignX("right").grow)

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
    override def valueChanged(e: ListSelectionEvent): Unit = {
      configToControls()
    }
  }

  private def fillConfigurationList(): Unit = {
    val configurationNames = configurationMap.keys.toArray
    lstConfigurations.setListData(configurationNames)
    if (configurationNames.nonEmpty) {
      lstConfigurations.setSelectedIndex(0)
    } else {
      configToControls()
    }
  }

  private def configToControls(): Unit = {

    val config = Option(lstConfigurations.getSelectedValue).flatMap(configurationMap.get(_))

    txtConfigName.setEnabled(config.nonEmpty)
    txtConfigName.setEditable(config.nonEmpty)
    txtConfigName.setText(config.map(_.name).getOrElse(""))

    chkFrom.setEnabled(config.nonEmpty)
    chkFrom.setSelected(config.exists(_.from.nonEmpty))

    datFrom.setVisible(chkFrom.isSelected)
    datFrom.setDate(config.flatMap(_.from.map(_.toIntDate)).getOrElse(LocalDate.now.toIntDate))

    chkTo.setEnabled(config.nonEmpty)
    chkTo.setSelected(config.exists(_.to.nonEmpty))

    datTo.setVisible(chkTo.isSelected)
    datTo.setDate(config.flatMap(_.to.map(_.toIntDate)).getOrElse(LocalDate.now.toIntDate))

    actionRefreshLabels.setEnabled(config.nonEmpty)

    lstLabels.setEnabled(config.nonEmpty)
    lstLabels.setListData(config.map(_.labels)
      .map { configLabels =>
        val transactions = context.getCurrentAccountBook.getTransactionSet.getAllTxns
        val existing = TxnUtil.getListOfAllUsedTransactionTags(transactions).asScala.toSet
        val labelsForList = (existing ++ additionalLabels ++ configLabels).toSeq.sorted(Ordering.String)
        labelsForList.map(label => new JCheckBox(label, configLabels.contains(label)))
      }
      .getOrElse(Set.empty)
      .toArray
    )
  }

  private def setLabelActiveInConfiguration(configName: String, label: String, active: Boolean) = {
    val actual = configurationMap(configName)
    if (active) {
      configurationMap += configName -> actual.copy(labels = actual.labels + label)
    } else {
      configurationMap += configName -> actual.copy(labels = actual.labels - label)
    }
  }

  private def newAdditionalLabel(): Unit = {
    //    val label = panAddLabel.txtNewLabel.getText()
    //    additionalLabels += label
    //    panAddLabel.txtNewLabel.setText("")
    //    Option(lstConfigurations.getSelectedValue).foreach(setLabelActiveInConfiguration(_, label, true))
    //    configToControls()
  }

  private def addConfiguration(): Unit = ???

  private def removeConfiguration(): Unit = ???

  private def renameConfiguration(): Unit = ???

  private def runConfiguration(): Unit = {
    // Before starting a run, save the current configuration (in case it changed)
    saveSettings()

    // todo: Perform the labelling run.

    val selected = lstLabels.getSelectedCheckBoxes.map(_.getText).mkString("\n")
    JOptionPane.showMessageDialog(this, selected, "Selected labels", INFORMATION_MESSAGE)
  }

  implicit class TraversableLikeExtensions[A, Repr](val underlying: TraversableLike[A, Repr]) {

    def map[That](f: PartialFunction[A, A])(implicit bf: CanBuildFrom[Repr, A, That]): That = {
      underlying.map(i => f.applyOrElse[A, A](i, _ => i))
    }

  }

  private def saveSettings(): Unit = {
    val currentConfig = lstConfigurations.getSelectedValue
    settings.update(s => s.copy(
      configurations = s.configurations.collect {
        case c if c.name == currentConfig =>
          c.copy(labels = lstLabels.getSelectedCheckBoxes.map(_.getText).toSet)
        case c => c
      }
    ))
  }

}
