package com.moneydance.modules.scalamd

import java.awt.Image
import java.awt.event.{WindowAdapter, WindowEvent}
import javax.swing.JFrame

import com.moneydance.apps.md.controller.FeatureModuleContext

trait SubFeature { self =>

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
    * Warm up the feature. This is called on application event `md:file:opened`.
    * eg: This is the time to check configuration and apply any automatic behaviour (automatically open a frame, start
    *     a listener, etc.).
    *
    * @param context the Moneydance context.
    */
  def initialize(context: SubFeatureContext): Unit = {}

  /**
    * Activate the functionality of the feature.
    * eg: This is the time to open the main frame of the feature.
    *
    * @param context the Moneydance context.
    */
  def invoke(context: SubFeatureContext): Unit

}

abstract class SingletonFrameSubFeature[F <: JFrame] extends SubFeature {

  private val lock = new Object

  protected def createFrame(context: SubFeatureContext): F

  private var _frame: Option[F] = None

  protected def frame: Option[F] = _frame

  override def invoke(context: SubFeatureContext): Unit = lock.synchronized {
    if (_frame.isEmpty) {
      _frame = Some {
        val f = createFrame(context)
        f.addWindowListener(new WindowAdapter {
          override def windowClosed(e: WindowEvent): Unit = lock.synchronized(_frame = None)
        })
        f
      }
    }
    _frame.foreach { f =>
      f.setVisible(true)
      f.toFront()
      f.requestFocus()
    }
  }

  def close(): Unit = lock.synchronized {
    _frame.foreach { f =>
      f.setVisible(false)
      f.dispose()
    }
    _frame = None
  }

}
