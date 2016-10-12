package com.moneydance.modules.features.mundane

import java.awt.Color
import java.awt.Color.{black, white}

import com.github.adeynack.scala.swing.MigPanel
import com.infinitekind.moneydance.model.ParentTxn
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil
import com.moneydance.modules.scalamd.Extensions._

import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.swing.FlowPanel.Alignment.{Left, Right}
import scala.swing.Swing._
import scala.swing._
import scala.swing.event.{Key, KeyPressed, WindowClosed}

class FullTextTransactionSearchWindowScalaSwing(
  context: FeatureModuleContext
) extends Frame {frame =>

  import FullTextTransactionSearchWindowScalaSwing._

  title = "Full Text Transaction Search"
  preferredSize = (1000, 600)
  AwtUtil.centerWindow(this.peer)

  reactions += {
    case WindowClosed(_) => dispose()
  }

  val actionClose = Action("Close")(dispose())

  contents = new MigPanel {

    //    constraints

    columns
      .grow.fill.gap
      .shrink

    rows
      .shrink.gap
      .grow.fill.gap
      .shrink

    val actionSearch = Action("Search")(performQuery())

    val txtSearchInput = new TextField {
      listenTo(keys)
      reactions += {
        case KeyPressed(_, Key.Enter, _, _) => actionSearch()
        case KeyPressed(_, Key.Escape, _, _) => actionClose()
      }
    }
    layout(txtSearchInput) = cc

    val btnSearch = new Button(actionSearch)
    layout(btnSearch) = cc.wrap

    val scrollResult = new ScrollPane(new MigPanel())
    layout(scrollResult) = cc.spanX.wrap

    val pnlButtons = new FlowPanel(Right)(
      new Button(actionClose)
    )
    layout(pnlButtons) = cc.spanX

    def performQuery(): Unit = {
      val query = txtSearchInput.text
      scrollResult.viewportView = new MigPanel {

        constraints.gridGap("4px", "2px")

        context.getCurrentAccountBook.getTransactionSet.asScala
          .collect { case t: ParentTxn => t }
          .filter { t =>
            t.getDescription.contains(query) ||
            t.getAttachmentKeys.asScala.exists(_.contains(query)) ||
            t.hasKeywordSubstring(query, false)
          }
          .foreach { t =>

            layout(new Label {
              text = t.getDateLD.toString
              opaque = true
              background = resultColorDate
              foreground = white
            }) = cc.growX

            layout(new Label {
              text = t.getDescription
              opaque = true
              background = resultColorDescription
              foreground = black
            }) = cc

            layout(new Label {
              text = t.getAccount.getFullAccountName
              opaque = true
              background = resultColorSource
              foreground = black
            }) = cc

            layout(new FlowPanel(Left)(
              Iterator.tabulate(t.getSplitCount)(t.getSplit).map { split =>
                new Label {
                  text = s"${split.getAmount / 100.0} to ${split.getAccount.getFullAccountName}"
                  opaque = true
                  background = resultColorDestination
                  foreground = white
                }
              }.toSeq: _*
            )) = cc.growX.wrap

          }
      }
    }

  }

}

object FullTextTransactionSearchWindowScalaSwing {

  private val resultColorDate = new Color(51, 98, 175)
  private val resultColorDescription = new Color(139, 179, 244)
  private val resultColorSource = new Color(192, 209, 237)
  private val resultColorDestination = new Color(5, 44, 107)

}