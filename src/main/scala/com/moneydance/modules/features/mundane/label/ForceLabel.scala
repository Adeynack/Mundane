package com.moneydance.modules.features.mundane.label

import java.time.LocalDate

import com.moneydance.modules.scalamd.{SingletonFrameSubFeature, SubFeatureContext}
import play.api.libs.json.Json

/**
  * One single configuration for the Force Label feature.
  *
  * @param name   name given to the configuration.
  * @param labels list of labels in which the user will have to chose.
  */
case class ForceLabelConfiguration(
  name: String,
  labels: Set[String],
  from: Option[LocalDate],
  to: Option[LocalDate]
)

/**
  * Global settings for the Force Label feature.
  */
case class ForceLabelSettings(
  configurations: Seq[ForceLabelConfiguration] = Seq.empty
)

object ForceLabel extends SingletonFrameSubFeature[ForceLabelSettingsFrame] {

  implicit val forceLabelConfigurationFormat = Json.format[ForceLabelConfiguration]

  implicit val forceLabelSettingsFormat = Json.format[ForceLabelSettings]

  override val name = "Force Label"

  override protected def createFrame(context: SubFeatureContext) = new ForceLabelSettingsFrame(
    context,
    context.getStorage("ForceLabel", ForceLabelSettings())
  )

}
