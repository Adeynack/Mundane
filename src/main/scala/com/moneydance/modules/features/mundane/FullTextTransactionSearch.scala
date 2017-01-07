package com.moneydance.modules.features.mundane

import java.awt.Color.{black, white}
import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.{Color, FlowLayout}
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing._

import com.github.adeynack.scala.swing.SimpleAction
import com.infinitekind.moneydance.model.ParentTxn
import com.infinitekind.util.DateUtil
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.features.mundane.FullTextTransactionSearch.Settings
import com.moneydance.modules.scalamd.{JsonLocalStorage, SingletonFrameSubFeature, Storage}
import net.miginfocom.layout.{AC, CC, LC}
import net.miginfocom.swing.MigLayout
import play.api.libs.json.{Format, Json}

import scala.collection.JavaConverters._

object FullTextTransactionSearch extends SingletonFrameSubFeature[FullTextTransactionSearchFrame] {

  case class Settings(
    lastSearchQuery: String = ""
  )

  implicit val settingsFormats: Format[Settings] = Json.format[Settings]

  override def name = "Full Text Transaction Search"

  override protected def createFrame(context: FeatureModuleContext) = new FullTextTransactionSearchFrame(
    context,
    new JsonLocalStorage(Main.localStorageKey("FullTextTransactionSearch"), Settings(), context)
  )

}

class FullTextTransactionSearchFrame(
  private val context: FeatureModuleContext,
  private val settings: Storage[Settings]
) extends JFrame {

  import FullTextTransactionSearchFrame._

  setDefaultCloseOperation(DISPOSE_ON_CLOSE)
  setTitle("Full Text Transaction Search")

  private val actionClose = SimpleAction("Close")(dispose)
  private val actionSearch = SimpleAction("Search")(performQuery)

  private val content = new JPanel(new MigLayout(
    new LC(),
    new AC()
      .grow.fill.gap
      .shrink,
    new AC()
      .shrink.gap
      .grow.fill.gap
      .shrink
  ))

  private val txtSearchInput = new JTextField()
  txtSearchInput.setText(settings.get.lastSearchQuery)
  txtSearchInput.setAction(actionSearch)
  txtSearchInput.addKeyListener(TxtSearchInputKeyListener)
  content.add(txtSearchInput)

  private val btnSearch = new JButton(actionSearch)
  content.add(btnSearch, new CC().wrap)

  private val scrResults = new JScrollPane(new JPanel())
  content.add(scrResults, new CC().spanX.wrap)

  private val pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT))
  pnlButtons.add(new JButton(actionClose))
  content.add(pnlButtons, new CC().spanX.wrap)

  setContentPane(content)
  setSize(1000, 600)
  AwtUtil.centerWindow(this)

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private object TxtSearchInputKeyListener extends KeyAdapter {

    override def keyPressed(e: KeyEvent): Unit = e.getKeyCode match {
      case KeyEvent.VK_ESCAPE => actionClose()
      case _ =>
    }

  }

  private def performQuery(): Unit = {
    val query = txtSearchInput.getText
    settings.update(_.copy(lastSearchQuery = query))
    val panResults = new JPanel(new MigLayout(
      new LC().gridGap("4px", "2px"),
      new AC(),
      new AC()
    ))
    context.getCurrentAccountBook.getTransactionSet.asScala
      .collect { case t: ParentTxn => t }
      .filter { (t: ParentTxn) =>
        t.getDescription.contains(query) ||
        t.getAttachmentKeys.asScala.exists(_.contains(query)) ||
        t.hasKeywordSubstring(query, false)
      }
      .foreach { (t: ParentTxn) =>

        var l = new JLabel(DateUtil.convertIntDateToLong(t.getDateInt).toString)
        l.setOpaque(true)
        l.setBackground(resultColorDate)
        l.setForeground(white)
        panResults.add(l, new CC().growX)

        l = new JLabel(t.getDescription)
        l.setOpaque(true)
        l.setBackground(resultColorDescription)
        l.setForeground(black)
        panResults.add(l)

        l = new JLabel(t.getAccount.getFullAccountName)
        l.setOpaque(true)
        l.setBackground(resultColorSource)
        l.setForeground(black)
        panResults.add(l)

        val panSplits = new JPanel(new FlowLayout(FlowLayout.LEFT))
        Iterator.tabulate(t.getSplitCount)(t.getSplit).foreach { split =>
          val l = new JLabel(s"${split.getAmount / 100.0} to ${split.getAccount.getFullAccountName}")
          l.setOpaque(true)
          l.setBackground(resultColorDestination)
          l.setForeground(white)
          panSplits.add(l)
        }
        panResults.add(panSplits, new CC().growX.wrap)
      }
    scrResults.setViewportView(panResults)
  }

}

object FullTextTransactionSearchFrame {

  private val resultColorDate = new Color(51, 98, 175)
  private val resultColorDescription = new Color(139, 179, 244)
  private val resultColorSource = new Color(192, 209, 237)
  private val resultColorDestination = new Color(5, 44, 107)

}