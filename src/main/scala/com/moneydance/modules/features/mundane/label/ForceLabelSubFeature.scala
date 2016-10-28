package com.moneydance.modules.features.mundane.label

import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.modules.scalamd.{JsonLocalStorage, SingletonFrameSubFeature}
import play.api.libs.json.Json

import scala.swing.Frame

/**
  * One single configuration for the Force Label feature.
  */
case class ForceLabelConfiguration(
  name: String,
  labels: Set[String]
)

/**
  * Global settings for the Force Label feature.
  */
case class ForceLabelSettings(
  configurations: Seq[ForceLabelConfiguration] = Seq.empty
)

object ForceLabelSubFeature extends SingletonFrameSubFeature[Frame] {

  implicit val forceLabelConfigurationFormat = Json.format[ForceLabelConfiguration]

  implicit val forceLabelSettingsFormat = Json.format[ForceLabelSettings]

  override def name: String = "Force Label"

  override protected def createFrame(context: FeatureModuleContext): Frame = {
    new ForceLabelConfigurationFrame(
      JsonLocalStorage[ForceLabelSettings](this, ForceLabelSettings(), context)
    )
  }

}
