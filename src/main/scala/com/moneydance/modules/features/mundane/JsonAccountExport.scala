package com.moneydance.modules.features.mundane

import java.awt.Font

import com.moneydance.apps.md.controller.FeatureModuleContext
import com.moneydance.modules.scalamd.MdJsonFormats._
import com.moneydance.modules.scalamd.SubFeature
import play.api.libs.json.Json

import scala.swing.{Frame, ScrollPane, TextArea}

object JsonAccountExport extends SubFeature {

  override def name = "Export account list to JSON in the clipboard"

  override def invoke(context: FeatureModuleContext): Unit = {
    new Frame() {
      contents = new ScrollPane(new TextArea(Json.prettyPrint(Json.toJson(context.getRootAccount))) {
        font = new Font("Courier New", Font.BOLD, 16)
      })
      visible = true
    }

  }

}
