package com.github.adeynack.scala.swing

import scala.swing.{Component, LayoutContainer}

/**
  * LCE stands for LayoutContainerExtended and named using the acronym
  * to keep code light.
  *
  * ```
  * content = new BorderLayout with LCE {
  * val btnOK = lay at Position.West a new Button(actionOk)
  * val labelTitle = lay at Position.North a new Label("Title")
  * }
  * ```
  */
trait LCE {self: LayoutContainer =>

  @inline def lay = new layObj()

  class layObj() {

    @inline def --(constraints: Constraints): aObj = at(constraints)

    @inline def at(constraints: Constraints): aObj = new aObj(constraints)

    class aObj(val constraints: Constraints) {

      @inline def --[T <: Component](component: T): T = a(component)

      @inline def a[T <: Component](component: T): T = {
        layout(component) = constraints
        component
      }

    }

  }

  //  def lay[T <: Component](constraints: Constraints, component: T) = lay -- constraints -- component

}
