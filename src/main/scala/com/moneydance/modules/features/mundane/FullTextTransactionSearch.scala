package com.moneydance.modules.features.mundane

import java.awt.Color.{black, white}
import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.{Color, Component, FlowLayout}
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing._

import com.github.adeynack.scala.swing.mig.MigPanel
import com.github.adeynack.scala.swing.{FlowPanel, SimpleAction}
import com.infinitekind.moneydance.model.ParentTxn
import com.infinitekind.util.DateUtil
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.features.mundane.FullTextTransactionSearch.FullTextTransactionSearchSettings
import com.moneydance.modules.scalamd.Extensions._
import com.moneydance.modules.scalamd.{SingletonFrameSubFeature, Storage, SubFeatureContext}
import play.api.libs.json.Json

import scala.collection.JavaConverters._

object FullTextTransactionSearch extends SingletonFrameSubFeature[FullTextTransactionSearchFrame] {

  case class FullTextTransactionSearchSettings(
    lastSearchQuery: String = ""
  )

  implicit val settingsFormats = Json.format[FullTextTransactionSearchSettings]

  override def name = "Full Text Transaction Search"

  override protected def createFrame(context: SubFeatureContext) = new FullTextTransactionSearchFrame(
    context,
    context.getStorage("FullTextTransactionSearch", FullTextTransactionSearchSettings())
  )

}

class FullTextTransactionSearchFrame(
  private val context: SubFeatureContext,
  private val settings: Storage[FullTextTransactionSearchSettings]
) extends JFrame {

  import FullTextTransactionSearchFrame._

  //
  // GUI
  //

  private val actionClose = SimpleAction("Close")(dispose)
  private val actionSearch = SimpleAction("Search")(performQuery)

  private val content = new MigPanel() {

    columns
      .grow.fill.gap
      .shrink

    rows
      .shrink.gap
      .grow.fill.gap
      .shrink

    val txtSearchInput = lay a new JTextField(settings.get.lastSearchQuery) {
      setAction(actionSearch)
      addKeyListener(TxtSearchInputKeyListener)
    }

    val btnSearch = lay at cc.wrap a new JButton(actionSearch)

    val scrResults = lay at cc.spanX.wrap a new JScrollPane(new JPanel())

    val pnlButtons = lay at cc.spanX.wrap a new FlowPanel(FlowLayout.RIGHT)() {
      lay a new JButton(actionClose)
    }

  }


  //
  // Constructor
  //

  setDefaultCloseOperation(DISPOSE_ON_CLOSE)
  setTitle("Full Text Transaction Search")

  setContentPane(content)
  setSize(1000, 600)
  AwtUtil.centerWindow(this)


  //
  // Methods
  //

  private object TxtSearchInputKeyListener extends KeyAdapter {

    override def keyPressed(e: KeyEvent): Unit = e.getKeyCode match {
      case KeyEvent.VK_ESCAPE => actionClose()
      case _ =>
    }

  }

  private def performQuery(): Unit = {
    val query = content.txtSearchInput.getText
    settings.update(_.copy(lastSearchQuery = query))
    content.scrResults.setViewportView(
      new MigPanel(_.gridGap("4px", "2px")) {
        context.getCurrentAccountBook.getTransactionSet.asScala
          .collect { case t: ParentTxn => t }
          .filter { t =>
            t.getDescription.contains(query) ||
            t.getAttachmentKeys.asScala.exists(_.contains(query)) ||
            t.hasKeywordSubstring(query, false)
          }
          .foreach { t =>

            def cell(t: String, bg: Color, fg: Color) = new JLabel(t) {
              setOpaque(true)
              setBackground(bg)
              setForeground(fg)
            }

            lay at cc.growX a cell(DateUtil.convertIntDateToLong(t.getDateInt).toString, resultColorDate, white)
            lay a cell(t.getDescription, resultColorDescription, black)
            lay a cell(t.getAccount.getFullAccountName, resultColorSource, black)
            lay at cc.growX.wrap a new FlowPanel(FlowLayout.LEFT)(t.splits.map { s =>
              cell(s"${s.getAmount / 100.0} to ${s.getAccount.getFullAccountName}", resultColorDestination, white).asInstanceOf[Component]
            }.toSeq: _*)
          }
      })
  }

}

object FullTextTransactionSearchFrame {

  private val resultColorDate = new Color(51, 98, 175)
  private val resultColorDescription = new Color(139, 179, 244)
  private val resultColorSource = new Color(192, 209, 237)
  private val resultColorDestination = new Color(5, 44, 107)

}