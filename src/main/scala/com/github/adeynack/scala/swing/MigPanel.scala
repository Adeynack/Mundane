package com.github.adeynack.scala.swing

import net.miginfocom.layout._
import net.miginfocom.swing.MigLayout

import scala.swing._

class MigPanel extends Panel
  with LayoutContainer
  with SequentialContainer.Wrapper
  with LCE {

  def layoutManager = peer.getLayout.asInstanceOf[MigLayout]

  override lazy val peer = new javax.swing.JPanel(new MigLayout(new LC(), new AC(), new AC())) with SuperMixin

  override type Constraints = CC

  override protected def constraintsFor(c: Component): CC = layoutManager.getComponentConstraints(c.peer).asInstanceOf[CC]

  override protected def areValid(c: CC): (Boolean, String) = (true, "")

  override protected def add(comp: Component, constraint: CC): Unit = peer.add(comp.peer, constraint)

  protected def add(comp: Component): Unit = add(comp, cc)

  def constraints: LC = layoutManager.getLayoutConstraints.asInstanceOf[LC]

  def constraints_=(c: LC) = layoutManager.setLayoutConstraints(c)

  def constraints(c: String) = layoutManager.setLayoutConstraints(ConstraintParser.parseLayoutConstraint(c))

  def columns: AC = layoutManager.getColumnConstraints.asInstanceOf[AC]

  def columns_=(c: AC) = layoutManager.setColumnConstraints(c)

  def columns(c: String) = layoutManager.setColumnConstraints(ConstraintParser.parseColumnConstraints(c))

  def rows: AC = layoutManager.getRowConstraints.asInstanceOf[AC]

  def rows_=(c: AC) = layoutManager.setRowConstraints(c)

  def rows(c: String) = layoutManager.setRowConstraints(ConstraintParser.parseRowConstraints(c))

  /**
    * Create a new [[CC]] writing less code. It then looks better when used.
    *
    * @return a brand new [[CC]] that can then be used to chain builder methods.
    */
  def cc = new CC()

}
