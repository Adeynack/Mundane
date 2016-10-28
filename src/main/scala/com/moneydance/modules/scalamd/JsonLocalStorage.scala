package com.moneydance.modules.scalamd

import com.moneydance.apps.md.controller.FeatureModuleContext
import play.api.libs.json._

trait Storage[T] {

  def set(value: T): Unit

  def update(updater: T => T): Unit

  def get: T

}

object JsonLocalStorage {

  def apply[T](source: AnyRef, default: => T, context: FeatureModuleContext)(implicit reads: Reads[T], writes: Writes[T]): JsonLocalStorage[T] = {
    val key = {
      val c = source.getClass.getName
      if (c.endsWith("$")) c.substring(0, c.length - 1)
      else c
    }
    new JsonLocalStorage[T](key, default, context)
  }

}

class JsonLocalStorage[T](val key: String, default: => T, context: FeatureModuleContext)(implicit reads: Reads[T], writes: Writes[T])
  extends Storage[T] {

  private val localStorage = context.getCurrentAccountBook.getLocalStorage

  def set(value: T): Unit = {
    val toJson = Json.toJson(value).toString
    localStorage.put(key, toJson)
  }

  def update(updater: T => T): Unit = {
    val current = get
    val modified = updater(current)
    if (modified != current) {
      set(modified)
    }
  }

  def get: T = {
    Option(localStorage.getString(key, null))
      .map { content =>
        Json.parse(content).validate[T] match {
          case JsSuccess(value, path) => value
          case JsError(failedPaths) =>
            System.err.println(s"""Failed to parse setting from local storage under key "$key":""")
            failedPaths.foreach { case (path, validationErrors) =>
              System.err.println(s"""  For JSON path "$path":""")
              validationErrors.foreach { validationError =>
                System.err.println(s"""    $validationError""")
              }
            }
            System.err.println(s"""  Using default: $default""")
            default
        }
      }
      .getOrElse {
        System.err.println(s"""No local storage value found for key "$key". Using default: $default""")
        default
      }
  }

}