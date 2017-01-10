package com.github.adeynack.scala.swing

import java.awt.Component
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import javax.swing._
import javax.swing.border.{Border, EmptyBorder}

import scala.collection.JavaConverters._

object CheckBoxList {
  protected val noFocusBorder: Border = new EmptyBorder(1, 1, 1, 1)
  protected val focusBorder = UIManager.getBorder("List.focusCellHighlightBorder")
}

class CheckBoxList extends JList[JCheckBox] { list =>

  import CheckBoxList._

  setCellRenderer(new ListCellRenderer[JCheckBox] {
    override def getListCellRendererComponent(list: JList[_ <: JCheckBox], value: JCheckBox, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component = {
      value.setBackground(if (isSelected) getSelectionBackground else getBackground)
      value.setForeground(if (isSelected) getSelectionForeground else getForeground)
      value.setEnabled(isEnabled)
      value.setFont(getFont)
      value.setFocusPainted(false)
      value.setBorder(if (isSelected) focusBorder else noFocusBorder)
      value
    }
  })

  addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent): Unit = {
      locationToIndex(e.getPoint) match {
        case -1 => // not an index
        case index =>
          val selected = getModel.getElementAt(index)
          list.setSelectedValue(null, false)
          selected.doClick()
      }
    }
  })

  addKeyListener(new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = e.getKeyCode match {
      case KeyEvent.VK_SPACE =>
        val selected: Seq[JCheckBox] = list.getSelectedValuesList.asScala
        list.setSelectedValue(null, false)
        val checked = selected.exists(_.isSelected == false)
        selected.filter(_.isSelected != checked).foreach(_.doClick)
      case _ =>
    }
  })

  def getSelectedCheckBoxes: Seq[JCheckBox] = {
    Iterator.tabulate(getModel.getSize)(getModel.getElementAt(_)).filter(_.isSelected).toSeq
  }

}
