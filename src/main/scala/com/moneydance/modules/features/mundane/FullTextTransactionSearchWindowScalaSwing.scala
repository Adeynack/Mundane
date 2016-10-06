package com.moneydance.modules.features.mundane

import scala.swing.Swing._
import java.awt.Color
import java.awt.Color.{BLACK, WHITE}
import java.awt.event.{InputEvent, KeyEvent}
import javax.swing.KeyStroke

import com.infinitekind.moneydance.model.ParentTxn
import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.awt.AwtUtil

import scala.collection.JavaConverters._
import scala.language.postfixOps
import scala.swing.FlowPanel.Alignment.{Left, Right}
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
      }
    }
    layout(txtSearchInput) = cc

    val btnSearch = new Button(actionSearch)
    layout(btnSearch) = cc.wrap

    val scrollResult = new ScrollPane(new MigPanel())
    layout(scrollResult) = cc.spanX.wrap

    val txtFilterCount = new Label() {
      private var counter = 0
      private def setCounter(v: Int) = {
        counter = v
        text = v.toString
      }
      def reset() = setCounter(0)
      def increment() = setCounter(counter + 1)
    }
    val pnlButtons = new FlowPanel(Right)(
      new Button(actionClose),
      txtFilterCount
    )
    layout(pnlButtons) = cc.spanX

    def performQuery(): Unit = {
      val query = txtSearchInput.text
      scrollResult.viewportView = new MigPanel {

        constraints.gridGap("4px", "2px")

        txtFilterCount.reset()
        context.getCurrentAccountBook.getTransactionSet.asScala
          .collect { case t: ParentTxn => t }
          .filter { t =>
            txtFilterCount.increment()
            t.getDescription.contains(query) ||
              t.getAttachmentKeys.asScala.exists(_.contains(query)) ||
              t.hasKeywordSubstring(query, false)
          }
          .foreach { t =>

            layout(new Label {
              text = {
                val d = t.getDateInt.toString
                val year = d.substring(0, 4)
                val month = d.substring(4, 6)
                val day = d.substring(6, 8)
                s"$year-$month-$day"
              }
              opaque = true
              background = resultColorDate
              foreground = WHITE
            }) = cc.growX

            layout(new Label {
              text = t.getDescription
              opaque = true
              background = resultColorDescription
              foreground = BLACK
            }) = cc

            layout(new Label {
              text = t.getAccount.getFullAccountName
              opaque = true
              background = resultColorSource
              foreground = BLACK
            }) = cc

            layout(new FlowPanel(Left)(
              Iterator.tabulate(t.getSplitCount)(t.getSplit).map { split =>
                new Label {
                  text = s"${split.getAmount / 100.0} to ${split.getAccount.getFullAccountName}"
                  opaque = true
                  background = resultColorDestination
                  foreground = WHITE
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