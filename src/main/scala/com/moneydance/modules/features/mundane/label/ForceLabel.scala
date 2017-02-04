package com.moneydance.modules.features.mundane.label

import java.time.LocalDate

import com.github.adeynack.scala.Utils
import com.moneydance.modules.scalamd.{SingletonFrameSubFeature, SubFeatureContext}
import play.api.libs.json.Json

object ForceLabel extends SingletonFrameSubFeature[ForceLabelSettingsFrame] {

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
  ) {
    override def toString: String = Json.toJson(this).toString
  }

  /**
    * Global settings for the Force Label feature.
    */
  case class ForceLabelSettings(
    configurations: Seq[ForceLabelConfiguration] = Seq.empty
  ) {
    override def toString: String = Utils.typedToString(this, Json.toJson(this))
  }

  implicit val forceLabelConfigurationFormat = Json.format[ForceLabelConfiguration]

  implicit val forceLabelSettingsFormat = Json.format[ForceLabelSettings]

  override val name = "Force Label"

  override protected def createFrame(context: SubFeatureContext) = new ForceLabelSettingsFrame(
    context,
    context.getStorage("ForceLabel", ForceLabelSettings())
  )

}
