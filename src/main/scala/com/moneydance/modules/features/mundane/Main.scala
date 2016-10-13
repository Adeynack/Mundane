package com.moneydance.modules.features.mundane

import java.awt.{Font, Image, Toolkit}
import java.io.ByteArrayOutputStream

import com.github.adeynack.scala.swing.FrameManager
import com.moneydance.apps.md.controller.{FeatureModule, Main => MdMain}
import play.api.libs.json._

class Main extends FeatureModule {

  import Main._

  private lazy val context = getContext.asInstanceOf[MdMain]
  private val fullTextTransactionSearch = new FrameManager(() => new FullTextTransactionSearchFrame(context))

  override def init(): Unit = {
    context.registerFeature(this, invokeStr.fullTextSearch, icon, "Full Text Transaction Search")
    context.registerFeature(this, invokeStr.accountsToJson, icon, "Export account list to JSON in the clipboard")
  }

  override def getName = "Mundane"

  val icon: Image = {
    val res = s"${getClass.getPackage.getName.replace(".", "/")}/icon.gif"
    Option(getClass.getClassLoader.getResourceAsStream(res)) map { in =>
      val bout = new ByteArrayOutputStream(1000)
      val buf = new Array[Byte](256)
      def getNext = in.read(buf, 0, buf.length)
      var n = getNext
      while (n >= 0) {
        bout.write(buf, 0, n)
        n = getNext
      }
      Toolkit.getDefaultToolkit.createImage(bout.toByteArray)
    } getOrElse {
      System.err.println(s"""Resource at "$res" was not found.""")
      null
    }
  }

  override def invoke(s: String) = s match {
    case invokeStr.fullTextSearch => fullTextTransactionSearch.show()
    case invokeStr.accountsToJson => exportToJson()
  }

  def exportToJson(): Unit = {
    import com.moneydance.modules.scalamd.MdJsonFormats._

    import scala.swing._
    new Frame() {
      contents = new ScrollPane(new TextArea(Json.prettyPrint(Json.toJson(getContext.getRootAccount))) {
        font = new Font("Courier New", Font.BOLD, 16)
      })
      visible = true
    }
  }

}

object Main {

  object invokeStr {
    val fullTextSearch = "fullTextSearch"
    val accountsToJson = "accountsToJson"
  }

}
