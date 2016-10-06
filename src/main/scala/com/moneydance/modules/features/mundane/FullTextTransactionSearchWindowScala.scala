package com.moneydance.modules.features.mundane

import java.awt.event.{ActionEvent, KeyAdapter, KeyEvent}
import java.awt.{Color, FlowLayout}
import javax.swing._

import com.infinitekind.moneydance.model.ParentTxn
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import net.miginfocom.layout.{AC, CC, LC}
import net.miginfocom.swing.MigLayout

import scala.collection.JavaConverters._

class FullTextTransactionSearchWindowScala(
  context: FeatureModuleContext
) extends JFrame("Full Text Transaction Search") {frame =>

  import FullTextTransactionSearchWindowScala._

  private val actionSearch = new AbstractAction("Search") {
    override def actionPerformed(e: ActionEvent): Unit = launchSearch()
  }

  private val actionClose = new AbstractAction("Close") {
    override def actionPerformed(e: ActionEvent): Unit = dispose()
  }

  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

  addKeyListener(new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = e.getKeyCode match {
      case KeyEvent.VK_ESCAPE => frame.dispose()
      case _ => super.keyPressed(e)
    }
  })

  val root = new JPanel(new MigLayout(
    new LC(),
    new AC()
      .grow.fill.gap
      .shrink,
    new AC()
      .shrink.gap
      .grow.fill.gap
      .shrink
  ))

  val txtSearchInput = new JTextField()
  txtSearchInput.addKeyListener(new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = e.getKeyCode match {
      case KeyEvent.VK_ENTER => actionSearch.actionPerformed(new ActionEvent(txtSearchInput, 0, ""))
      case _ => super.keyPressed(e)
    }
  })
  root.add(txtSearchInput, new CC())

  val btnSearch = new JButton(actionSearch)
  btnSearch.setMnemonic('s')
  root.add(btnSearch, new CC().wrap)

  val scrollResults = new JScrollPane(new JPanel())
  root.add(scrollResults, new CC().spanX.wrap)

  val pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT))

  val btnClose = new JButton(actionClose)
  pnlButtons.add(btnClose)

  root.add(pnlButtons, new CC().spanX)

  setSize(1000, 400)
  getContentPane.add(root)
  AwtUtil.centerWindow(this)

  private def launchSearch() = {
    val query = txtSearchInput.getText()
    scrollResults.setViewportView(new JPanel(new MigLayout(
      new LC().gridGap("4px", "2px"),
      new AC(),
      new AC()
    )) {
      context.getCurrentAccountBook.getTransactionSet.asScala
        .collect { case t: ParentTxn => t }
        .filter { t =>
          t.getDescription.contains(query) ||
            t.getAttachmentKeys.asScala.exists(_.contains(query)) ||
            t.hasKeywordSubstring(query, false)
        }
        .foreach { t =>

          add(new JLabel {
            setText {
              val d = t.getDateInt.toString
              val year = d.substring(0, 4)
              val month = d.substring(4, 6)
              val day = d.substring(6, 8)
              s"$year-$month-$day"
            }
            setOpaque(true)
            setBackground(resultColorDate)
            setForeground(Color.WHITE)
          }, new CC().growX)

          add(new JLabel {
            setText(t.getDescription)
            setOpaque(true)
            setBackground(resultColorDescription)
            setForeground(Color.BLACK)
          }, new CC().growX)

          add(new JLabel {
            setText(t.getAccount.getFullAccountName)
            setOpaque(true)
            setBackground(resultColorSource)
            setForeground(Color.BLACK)
          }, new CC().growX)

          add(new JPanel {
            setLayout(new FlowLayout(FlowLayout.LEFT))
            Iterator.tabulate(t.getSplitCount)(t.getSplit).foreach { split =>
              add(new JLabel {
                setText(s"${split.getAmount / 100.0} to ${split.getAccount.getFullAccountName}")
                setOpaque(true)
                setBackground(resultColorDestination)
                setForeground(Color.WHITE)
              })
            }
          }, new CC().growX.wrap)
        }
    })
  }

}

object FullTextTransactionSearchWindowScala {

  private val resultColorDate = new Color(51, 98, 175)
  private val resultColorDescription = new Color(139, 179, 244)
  private val resultColorSource = new Color(192, 209, 237)
  private val resultColorDestination = new Color(5, 44, 107)

}