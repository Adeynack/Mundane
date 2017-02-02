package com.github.adeynack.scala.swing.mig

import javax.swing.JPanel

import com.github.adeynack.scala.Utils._
import com.github.adeynack.scala.swing.JPanelWithLayout
import net.miginfocom.layout.{AC, CC, LC}
import net.miginfocom.swing.MigLayout

class MigPanel(
  val layoutConstraints: LC,
  val columns: AC,
  val rows: AC
) extends JPanel(new MigLayout(layoutConstraints, columns, rows))
  with JPanelWithLayout[CC] { thisPanel =>

  def this() = this(new LC, new AC, new AC)

  def this(
    layoutConstraints: (LC) => Unit = _ => Unit,
    columnsConstraints: (AC) => Unit = _ => Unit,
    rowsConstraints: (AC) => Unit = _ => Unit
  ) = this(init(new LC)(layoutConstraints), init(new AC)(columnsConstraints), init(new AC)(rowsConstraints))

  /**
    * Shortcut for creating a new [[CC]].
    *
    * {{{
    *
    *   lay at cc.growX.wrap a new JButton
    *
    *   // instead of
    *
    *   lay at new CC().wrap a new JButton
    *
    * }}}
    *
    * @return a new [[CC]]
    */
  protected def cc = new CC()

}
