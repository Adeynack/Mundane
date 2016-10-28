package com.moneydance.modules.scalamd

import java.awt.Image

import com.moneydance.apps.md.controller.FeatureModuleContext
import play.api.libs.json.{JsObject, JsValue}

import scala.swing.Frame
import scala.swing.event.WindowClosed

trait SubFeature {self =>

  /**
    * @return the name used for this feature in the GUI.
    */
  def name: String

  /**
    * @return a string key identifying the feature (inside of Mundane).
    */
  def key: String = self.getClass.getName

  /**
    * @return the image for the feature -or- [[None]] for using a default image.
    */
  def image: Option[Image] = None

  /**
    * Activate the functionality of the feature.
    *
    * @param context the Moneydance context.
    */
  def invoke(context: FeatureModuleContext): Unit

}

abstract class SingletonFrameSubFeature[F <: Frame] extends SubFeature {

  protected def createFrame(context: FeatureModuleContext): F

  private var _frame: Option[F] = None

  protected def frame: Option[F] = _frame

  override def invoke(context: FeatureModuleContext): Unit = this.synchronized {
    if (_frame.isEmpty) {
      _frame = Some {
        val f = createFrame(context)
        f.reactions += {
          case WindowClosed(_) => this.synchronized(_frame = None)
        }
        f
      }
    }
    _frame.foreach { f =>
      f.visible = true
      f.peer.toFront()
      f.peer.requestFocus()
    }
  }

  def close(): Unit = this.synchronized {
    _frame.foreach { f =>
      f.visible = false
      f.dispose()
    }
    _frame = None
  }

}
