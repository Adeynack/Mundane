package com.moneydance.modules.features.mundane

import java.awt.Font
import javax.swing._

import com.moneydance.modules.scalamd.MdJsonFormats._
import com.moneydance.modules.scalamd.{SubFeature, SubFeatureContext}
import play.api.libs.json.Json

object JsonAccountExport extends SubFeature {

  override def name = "Export account list to JSON in the clipboard"

  override def invoke(context: SubFeatureContext): Unit = {
    new JFrame() {
      setContentPane(new JScrollPane(new JTextArea {
        setText(Json.prettyPrint(Json.toJson(context.getRootAccount)))
        setFont(new Font("Courier New", Font.BOLD, 12))
      }))
      pack()
      setVisible(true)
    }

  }

}
