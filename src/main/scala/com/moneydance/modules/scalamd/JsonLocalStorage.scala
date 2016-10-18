package com.moneydance.modules.scalamd

import com.moneydance.apps.md.controller.FeatureModuleContext
import play.api.libs.json._

object JsonLocalStorage {

  def apply[T](source: AnyRef, default: => T)(implicit context: FeatureModuleContext, reads: Reads[T], writes: Writes[T]): JsonLocalStorage[T] = {
    val key = source.getClass.getName
    new JsonLocalStorage[T](key, default)
  }

}

class JsonLocalStorage[T](val key: String, default: => T)(implicit context: FeatureModuleContext, reads: Reads[T], writes: Writes[T]) {

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
        System.out.println(s"""No local storage value found for key "$key". Using default: $default""")
        default
      }
  }

}