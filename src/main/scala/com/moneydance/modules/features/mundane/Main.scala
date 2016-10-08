package com.moneydance.modules.features.mundane

import java.awt.{Image, Toolkit}
import java.io.ByteArrayOutputStream

import com.infinitekind.moneydance.model.Account
import com.moneydance.apps.md.controller.FeatureModule
import play.api.libs.json._

import scala.collection.JavaConverters._
import scala.swing.FrameManager

class Main extends FeatureModule {

  import Main._

  private val fullTextTransactionSearch = new FrameManager(() => new FullTextTransactionSearchWindowScalaSwing(getContext))

  override def init(): Unit = {
    val context = getContext
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
    import scala.swing._
    import Main.accountWrites
    new Frame() {
      contents = new ScrollPane(new TextArea(Json.prettyPrint(Json.toJson(getContext.getRootAccount))))
      visible = true
    }
  }

}

object Main {

  object invokeStr {
    val fullTextSearch = "fullTextSearch"
    val accountsToJson = "accountsToJson"
  }

  implicit val accountWrites: Writes[Account] = new Writes[Account] {
    def writes(a: Account): JsValue = Json.obj(
      "name" -> a.getAccountName,
      "subAccounts" -> a.getSubAccounts.asScala.map(writes)
    )
  }

}
