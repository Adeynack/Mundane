package com.moneydance.modules.scalamd

import java.awt.Image

import com.infinitekind.moneydance.model.{Account, AccountBook}
import com.moneydance.apps.md.controller.{FeatureModule, FeatureModuleContext}
import com.moneydance.apps.md.extensionapi.AccountEditor
import com.moneydance.apps.md.view.HomePageView
import play.api.libs.json.{Reads, Writes}

class SubFeatureContext(baseContext: FeatureModuleContext) extends FeatureModuleContext {

  //
  // Logging
  //

  def info(message: String): Unit = {
    System.err.println(s"[INFO] $message")
  }

  def error(message: String, error: Throwable = null): Unit = {
    System.err.println(s"[ERROR] $message")
    if (error != null) {
      System.err.println(error.toString)
    }
  }

  //
  // Storage
  //

  def getStorage[T](subKey: String, default: => T)(implicit reads: Reads[T], writes: Writes[T]): Storage[T] =
    new JsonLocalStorage[T](s"Mundane:$subKey", default, this)

  //
  // Route to the base context
  //

  override def getCurrentAccountBook: AccountBook = baseContext.getCurrentAccountBook

  override def registerFeature(featureModule: FeatureModule, key: String, icon: Image, displayName: String): Unit =
    baseContext.registerFeature(featureModule, key, icon, displayName)

  override def getBuild: Int = baseContext.getBuild

  override def showURL(s: String): Unit = baseContext.showURL(s)

  override def getVersion: String = baseContext.getVersion

  override def registerHomePageView(featureModule: FeatureModule, homePageView: HomePageView): Unit =
    baseContext.registerHomePageView(featureModule, homePageView)

  override def registerAccountEditor(featureModule: FeatureModule, i: Int, accountEditor: AccountEditor): Unit =
    baseContext.registerAccountEditor(featureModule, i, accountEditor)

  override def getRootAccount: Account = baseContext.getRootAccount

}
