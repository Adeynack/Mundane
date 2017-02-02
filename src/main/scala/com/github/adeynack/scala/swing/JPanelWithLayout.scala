package com.github.adeynack.scala.swing

import java.awt.{BorderLayout, Component, FlowLayout}
import javax.swing.{Action, JButton, JPanel, JScrollPane}

trait JPanelWithLayout[Constraint] { thisPanel: JPanel =>

  object lay {

    def a[T <: Component](component: T): T = {
      thisPanel.add(component)
      component
    }

    def aScrolled[T <: Component](component: T): T = {
      thisPanel.add(new JScrollPane(component))
      component
    }

    def at(constraints: Constraint) = new {

      def a[T <: Component](component: T): T = {
        thisPanel.add(component, constraints)
        component
      }

      def aScrolled[T <: Component](component: T): T = {
        thisPanel.add(new JScrollPane(component), constraints)
        component
      }

    }
  }

}

class BorderPanel(hgap: Int = 0, vgap: Int = 0)
  extends JPanel(new BorderLayout(hgap, vgap))
    with JPanelWithLayout[String]

class FlowPanel(align: Int = FlowLayout.CENTER, hgap: Int = 5, vgap: Int = 5)(content: Component*)
  extends JPanel(new FlowLayout(align, hgap, vgap))
    with JPanelWithLayout[Int] {

  content.foreach(add)

}

class ButtonFlowPanel(align: Int = FlowLayout.CENTER, hgap: Int = 5, vgap: Int = 5)(actions: Action*)
  extends FlowPanel(align, hgap, vgap)(actions.map(new JButton(_).asInstanceOf[Component]): _*)