package com.moneydance.modules.features.mundane

import com.github.adeynack.scala.Utils._

import java.awt.{Image, Toolkit}
import java.io.ByteArrayOutputStream
import javax.swing.SwingUtilities

import com.infinitekind.moneydance.model._
import com.moneydance.apps.md.controller.FeatureModule
import com.moneydance.modules.features.mundane.label.ForceLabel
import com.moneydance.modules.features.mundane.logViewer.LogViewer
import com.moneydance.modules.scalamd.{SubFeature, SubFeatureContext}

import scala.collection.breakOut

class Main extends FeatureModule {

  private lazy val context = new SubFeatureContext(getContext)

  private val features: Map[String, SubFeature] = Seq[SubFeature](
    ForceLabel,
    FullTextTransactionSearch,
    JsonAccountExport,
    LogViewer
  ).map(f => f.key -> f)(breakOut)

  override def init(): Unit = {
    features.foreach { case (k: String, f: SubFeature) =>
      context.registerFeature(this, k, f.image.getOrElse(icon), f.name)
    }
  }

  private val accountBookListener = new AccountBookListener {

    override def accountBookDataUpdated(accountBook: AccountBook): Unit =
      context.info(s"AccountBookListener::accountBookDataUpdated accountBook = $accountBook")

    override def accountBookDataReplaced(accountBook: AccountBook): Unit =
      context.info(s"AccountBookListener::accountBookDataReplaced accountBook = $accountBook")

  }

  private val accountListener = new AccountListener {

    override def accountAdded(account: Account, account1: Account): Unit =
      context.info(s"AccountListener::accountAdded account = $account account1 = $account1")

    override def accountDeleted(account: Account, account1: Account): Unit =
      context.info(s"AccountListener::accountDeleted account = $account account1 = $account1")

    override def accountBalanceChanged(account: Account): Unit =
      context.info(s"AccountListener::accountBalanceChanged account = $account")

    override def accountModified(account: Account): Unit =
      context.info(s"AccountListener::accountModified account = $account")

  }

  val fileListener = new MDFileListener {

    override def dirtyStateChanged(account: Account): Unit =
      context.info(s"MDFileListener::dirtyStateChanged account = $account")

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
      context.info(s"""Resource at "$res" was not found.""")
      null
    }
  }

  override def invoke(s: String): Unit = features(s).invoke(context)

  override def handleEvent(appEvent: String): Unit = {
    appEvent match {
      case "md:account:root" =>
        context.info(s"Main::handleEvent appEvent = $appEvent")
        context.getCurrentAccountBook.addListener(accountBookListener)
        context.getCurrentAccountBook.addAccountListener(accountListener)
        context.getCurrentAccountBook.addFileListener(fileListener)

      case "md:file:opened" =>
        // Call the `initialize` of every feature.
        features.values.foreach(f => SwingUtilities.invokeLater {
          f.initialize(context)
        })

        // todo : Remove this (there for debugging reasons)
        SwingUtilities.invokeAndWait(invoke(ForceLabel.key))

      case _ =>
        context.info(s"Main::handleEvent appEvent = $appEvent")
    }
    super.handleEvent(appEvent)
  }
}

object Main {

  def localStorageKey(suffix: String) = s"Mundane:$suffix"

}