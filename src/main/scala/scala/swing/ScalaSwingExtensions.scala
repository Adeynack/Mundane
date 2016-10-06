//package scala.swing
//
//object ScalaSwingExtensions {
//
//  trait LayoutContainerExtensions {self: LayoutContainer =>
//
//    implicit class ComponentExtensions[TComponent <: Component](val component: TComponent) {
//
//      @inline def layout(): TComponent = {
//        LayoutContainerExtensions.this.add(component, null)
//        component
//      }
//
//      @inline def layout(constraints: Constraints): TComponent = {
//        LayoutContainerExtensions.this.layout(component) = constraints
//        component
//      }
//
//    }
//
//    implicit class ConstraintsExtensions(constraints: Constraints) {
//
//      @inline def lay[C <: Component](component: C): C = {
//        LayoutContainerExtensions.this.add(component, constraints)
//      }
//    }
//
//  }
//
//}
