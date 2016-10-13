package com.moneydance.modules.scalamd

import java.io.{File, PrintWriter}

import com.moneydance.apps.md.controller.{UserPreferences, Main => MdMain}
import play.api.libs.json._

import scala.io.Source
import scala.util.{Failure, Success, Try}

sealed trait Setting[V] {

  def key: String

  def default: V

  def set(value: V): Unit

  def get: V

}

class StringSetting(val key: String, val default: String = "")(implicit preferences: UserPreferences) extends Setting[String] {

  override def set(value: String): Unit = preferences.setSetting(key, value)

  override def get: String = preferences.getSetting(key, default)

}

class BooleanSetting(val key: String, val default: Boolean = false)(implicit preferences: UserPreferences) extends Setting[Boolean] {

  override def set(value: Boolean): Unit = preferences.setSetting(key, value)

  override def get: Boolean = preferences.getBoolSetting(key, default)

}

class LongSetting(val key: String, val default: Long = 0L)(implicit preferences: UserPreferences) extends Setting[Long] {

  override def set(value: Long): Unit = preferences.setSetting(key, value)

  override def get: Long = preferences.getLongSetting(key, default)

}

trait JsonSetting[T] {

  def set(value: T): Unit

  def update(updater: T => T): Unit

  def get: T

}

class JsonFileSetting[T](val filePath: String, default: => T)(implicit reads: Reads[T], writes: Writes[T]) extends JsonSetting[T] {

  override def set(value: T): Unit = {
    val toJson = Json.prettyPrint(Json.toJson(value))
    Try {
      val file = new File(filePath)
      file.getParentFile.mkdirs()
      new PrintWriter(file)
    } match {
      case Failure(e) =>
        System.err.println(s"""Unable to write to file "$filePath" because of a ${e.getClass.getName}: ${e.getMessage}""")
      case Success(writer) =>
        try {
          writer.write(toJson)
        } finally {
          writer.close()
        }
    }
  }

  override def update(updater: (T) => T): Unit = {
    val current = get
    val modified = updater(current)
    if (modified != current) {
      set(modified)
    }
  }

  override def get: T = {
    val file = new File(filePath)
    if (!file.exists()) {
      default
    } else {
      try {
        val content = Source.fromFile(file).mkString
        Json.parse(content).validate[T] match {
          case JsSuccess(value, path) => value
          case JsError(failedPaths) =>
            System.err.println(s"""Failed to parse setting from file "$filePath":""")
            failedPaths.foreach { case (path, validationErrors) =>
              System.err.println(s"""  For JSON path "$path":""")
              validationErrors.foreach { validationError =>
                System.err.println(s"""    $validationError""")
              }
            }
            default
        }
      } catch {
        case e: Throwable =>
          default
      }
    }
  }

}

object JsonFileSetting {

  def apply[T](source: AnyRef, default: => T)(implicit md: MdMain, reads: Reads[T], writes: Writes[T]): JsonFileSetting[T] = {
    val filePrefix = source.getClass.getName.replace('$', '_')
    val baseFolder = md.getPlatformHelper.getRootPath
    val filePath = s"$baseFolder/mundane/$filePrefix.json"
    new JsonFileSetting[T](filePath, default)
  }

}

class JsonMdSetting[T](
  val key: String,
  defaultValue: => T
)(implicit preferences: UserPreferences,
  reads: Reads[T],
  writes: Writes[T]
) extends Setting[T]
  with JsonSetting[T] {

  override lazy val default: T = defaultValue

  override def set(value: T): Unit = preferences.setSetting(key, Json.toJson(value).toString)

  def update(updater: T => T): Unit = {
    val current = get
    set(updater(current))
  }

  override def get: T = {
    preferences.getSetting(key, "") match {
      case "" => default
      case representation => Json.parse(representation).validate[T] match {
        case JsSuccess(value, path) => value
        case JsError(failedPaths) =>
          System.err.println(s"""Failed to parse setting "$key":""")
          failedPaths.foreach { case (path, validationErrors) =>
            System.err.println(s"""  For path "$path":""")
            validationErrors.foreach { validationError =>
              System.err.println(s"""    $validationError""")
            }
          }
          default
      }
    }
  }

}