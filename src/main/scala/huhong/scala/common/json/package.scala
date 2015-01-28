package huhong.scala.common

import java.io.{StringWriter, Writer, OutputStream, Serializable}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.{JsonParser, GsonBuilder}
import net.liftweb.json.JsonAST.JArray

import net.liftweb.json._
import net.liftweb.json.JsonParser._
import net.liftweb.json.Serialization._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * Created by huhong on 14/12/18.
 */
package object json {

  var jsonHandler = "gson" //"lift"


  var jsonEncoding = "utf-8"


  def NonNullFieldSerializer[T: Manifest] = FieldSerializer[T](serializer = nonNullSerializer)


  def JavaAnyFieldSerializer = FieldSerializer[Any](serializer = JavaSerializer)

  def AnyRefFieldSerializer = FieldSerializer[AnyRef]()

  def NonNullJavaAnyRefFieldSerializer = NonNullFieldSerializer[AnyRef]


  private val JavaSerializer: PartialFunction[(String, Any), Option[(String, Any)]] = {
    case (name, list: java.util.Collection[_]) => {
      Some(name, list.asScala)
    }
    case (name, map: java.util.Map[_, _]) => {
      Some(name, map.asScala)
    }
    case (name, any) => Some((name, any))
  }
  private val nonNullSerializer: PartialFunction[(String, Any), Option[(String, Any)]] = {
    case (name, any) if any == null => {
      None
    }
    case (name, list: java.util.Collection[_]) => {
      Some(name, list.asScala)
    }
    case (name, map: java.util.Map[_, _]) => {
      Some(name, map.asScala)
    }
    case (name, any) => Some((name, any))
  }

  //java collection的反序列化请参见http://www.cakesolutions.net/teamblogs/2012/06/29/lift-json-java-util-set
  implicit var formats = DefaultFormats + JavaAnyFieldSerializer

  val objectMapper = new ObjectMapper
  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
  objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
  objectMapper.registerModule(DefaultScalaModule)


  lazy val gson = new GsonBuilder().serializeNulls().create()

  private lazy val gsonFormat = new GsonBuilder().setPrettyPrinting().create()
  private lazy val jsonParser = new JsonParser

  implicit def JsonEx(bean: AnyRef) = new {
    def toJson(out: OutputStream) = {

      jsonHandler match {
        case "gson" => {
          val jsonStrWriter = new StringWriter()
          gson.toJson(bean, jsonStrWriter)
          out.write(jsonStrWriter.toString.getBytes(jsonEncoding))
        }
        case "lift" => {
          val jsonStrWriter = new StringWriter()
          write(bean, jsonStrWriter)
          out.write(jsonStrWriter.toString.getBytes(jsonEncoding))
        }
        case _ => {
          objectMapper.writeValue(out, bean)
        }
      }


    }

    def toJson(writer: Writer) = {

      jsonHandler match {
        case "gson" => {
          gson.toJson(bean, writer)
        }
        case "lift" => {
          write(bean, writer)
        }
        case _ => {
          objectMapper.writeValue(writer, bean)
        }
      }

    }


    def toJsonString(): String = {
      val sw = new StringWriter()
      toJson(sw)
      try {
        sw.toString
      }
      finally {
        sw.close();
      }
    }
  }

  implicit def StringEx(str: String) = new {
    def formatJsonString(): String = {
      gsonFormat.toJson(jsonParser.parse(str))
    }

    def toBean[T: Manifest](): T = {

      jsonHandler match {
        case "gson" => {
          gson.fromJson(str, manifest[T].runtimeClass).asInstanceOf[T]
        }
        case "lift" => {
          net.liftweb.json.parse(str).extract[T]
        }
        case _ => {
          objectMapper.readValue(str, manifest[T].runtimeClass).asInstanceOf[T]
        }
      }

    }

    def toBean[T: Manifest](clz: Class[T]): T = {

      jsonHandler match {
        case "gson" => {
          gson.fromJson(str, clz)
        }
        case "lift" => {
          net.liftweb.json.parse(str).extract[T]
        }
        case _ => {
          objectMapper.readValue(str, manifest[T].runtimeClass).asInstanceOf[T]
        }
      }

    }
  }
}
