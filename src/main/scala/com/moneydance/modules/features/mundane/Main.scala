package com.moneydance.modules.features.mundane

import java.awt.{Font, Image, Toolkit}
import java.io.ByteArrayOutputStream

import com.github.adeynack.scala.swing.FrameManager
import com.infinitekind.moneydance.model._
import com.moneydance.apps.md.controller.FeatureModule
import play.api.libs.json._

class Main extends FeatureModule {

  import Main._

  private implicit lazy val context = getContext
  private val fullTextTransactionSearch = new FrameManager(() => new FullTextTransactionSearchFrame)


  override def init(): Unit = {
    context.registerFeature(this, invokeStr.fullTextSearch, icon, "Full Text Transaction Search")
    context.registerFeature(this, invokeStr.accountsToJson, icon, "Export account list to JSON in the clipboard")
  }

  private val accountBookListener = new AccountBookListener {

    override def accountBookDataUpdated(accountBook: AccountBook): Unit =
      System.err.println(s"AccountBookListener::accountBookDataUpdated accountBook = $accountBook")

    override def accountBookDataReplaced(accountBook: AccountBook): Unit =
      System.err.println(s"AccountBookListener::accountBookDataReplaced accountBook = $accountBook")

  }

  private val accountListener = new AccountListener {

    override def accountAdded(account: Account, account1: Account): Unit =
      System.err.println(s"AccountListener::accountAdded account = $account account1 = $account1")

    override def accountDeleted(account: Account, account1: Account): Unit =
      System.err.println(s"AccountListener::accountDeleted account = $account account1 = $account1")

    override def accountBalanceChanged(account: Account): Unit =
      System.err.println(s"AccountListener::accountBalanceChanged account = $account")

    override def accountModified(account: Account): Unit =
      System.err.println(s"AccountListener::accountModified account = $account")

  }

  val fileListener = new MDFileListener {

    override def dirtyStateChanged(account: Account): Unit =
      System.err.println(s"MDFileListener::dirtyStateChanged account = $account")

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

  override def handleEvent(appEvent: String): Unit = {
    appEvent match {
      case "md:account:root" =>
        System.err.println(s"Main::handleEvent appEvent = $appEvent")
        context.getCurrentAccountBook.addListener(accountBookListener)
        context.getCurrentAccountBook.addAccountListener(accountListener)
        context.getCurrentAccountBook.addFileListener(fileListener)
      case _ =>
        System.err.println(s"Main::handleEvent appEvent = $appEvent")
    }
    super.handleEvent(appEvent)
  }
}

object Main {

  object invokeStr {
    val fullTextSearch = "fullTextSearch"
    val accountsToJson = "accountsToJson"
  }

}
