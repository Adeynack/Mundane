package com.moneydance.modules.features.mundane.label

import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.modules.features.mundane.Main
import com.moneydance.modules.scalamd.{JsonLocalStorage, SingletonFrameSubFeature}
import play.api.libs.json.Json

import scala.swing.Frame

/**
  * One single configuration for the Force Label feature.
  *
  * @param name   name given to the configuration.
  * @param labels list of labels in which the user will have to chose.
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

object ForceLabel extends SingletonFrameSubFeature[Frame] {

  implicit val forceLabelConfigurationFormat = Json.format[ForceLabelConfiguration]

  implicit val forceLabelSettingsFormat = Json.format[ForceLabelSettings]

  override def name = "Force Label"

  override protected def createFrame(context: FeatureModuleContext): Frame = {
    new ForceLabelSettingsFrame(
      context,
      new JsonLocalStorage[ForceLabelSettings](Main.localStorageKey("ForceLabel"), ForceLabelSettings(), context)
    )
  }

}
