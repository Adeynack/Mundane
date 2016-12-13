package com.moneydance.modules.scalamd

import com.moneydance.apps.md.controller.FeatureModuleContext
import play.api.libs.json._

trait Storage[T] {

  def set(value: T): Unit

  def update(updater: T => T): Unit

  def get: T

}

class JsonLocalStorage[T](val key: String, default: => T, context: FeatureModuleContext)(implicit reads: Reads[T], writes: Writes[T])
  extends Storage[T] {

  private val localStorage = context.getCurrentAccountBook.getLocalStorage

  private var cached: Option[T] = None

  def set(value: T): Unit = {
    val toJson = Json.toJson(value)
    System.err.println(s"""Saving to local storage with key "$key" and value ${Json.prettyPrint(toJson)}""")
    localStorage.put(key, toJson.toString)
    cached = Some(value)
  }

  def update(updater: T => T): Unit = {
    val current = get
    val modified = updater(current)
    if (modified != current) {
      set(modified)
    }
  }

  def get: T = cached.getOrElse {
    val storageValue =
      Option(localStorage.getString(key, null))
        .map { content =>
          val contentJsVal = Json.parse(content)
          contentJsVal.validate[T] match {
            case JsSuccess(value, _) =>
              System.err.println(s"""Loaded local storage from key "$key". Read value ${Json.prettyPrint(contentJsVal)}""")
              value
            case JsError(failedPaths) =>
              System.err.println(s"""Failed to parse setting from local storage under key "$key" with value ${Json.prettyPrint(contentJsVal)}""")
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
    cached = Some(storageValue)
    storageValue
  }

}