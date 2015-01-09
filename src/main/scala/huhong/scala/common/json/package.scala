package huhong.scala.common

import java.io.{StringWriter, Writer, OutputStream, Serializable}

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.{JsonParser, GsonBuilder}

import net.liftweb.json._
import net.liftweb.json.JsonParser._
import net.liftweb.json.Serialization._

/**
 * Created by huhong on 14/12/18.
 */
package object json {

  var jsonHandler = "lift"
  var jsonEncoding = "utf-8"


  def NonNullFieldSerializer[T: Manifest] = FieldSerializer[T](serializer = nonNullSerializer)


  def AnyRefFieldSerializer = FieldSerializer[AnyRef]()

  def NonNullAnyRefFieldSerializer = NonNullFieldSerializer[AnyRef]

  private val nonNullSerializer: PartialFunction[(String, Any), Option[(String, Any)]] = {
    case (name, any) if any == null => {
      None
    }
    case (name, any) => Some((name, any))
  }

  implicit var formats = DefaultFormats + NonNullAnyRefFieldSerializer

  val objectMapper = new ObjectMapper
  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
  objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
  objectMapper.registerModule(DefaultScalaModule)


  private lazy val gson = new GsonBuilder().setPrettyPrinting().create()
  private lazy val jsonParser = new JsonParser

  implicit def JsonEx(bean: AnyRef) = new {
    def toJson(out: OutputStream) = {
      if (jsonHandler.equals("lift")) {
        val jsonStrWriter = new StringWriter()
        write(bean, jsonStrWriter)
        out.write(jsonStrWriter.toString.getBytes(jsonEncoding))
      } else {
        objectMapper.writeValue(out, bean)

      }
    }

    def toJson(writer: Writer) = {
      if (jsonHandler.equals("lift")) {
        write(bean, writer)
      } else
        objectMapper.writeValue(writer, bean)
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
      gson.toJson(jsonParser.parse(str))
    }

    def toBean[T: Manifest](): T = {
      if (jsonHandler.equals("lift")) {
        net.liftweb.json.parse(str).extract[T]
      } else
        objectMapper.readValue(str, manifest[T].runtimeClass).asInstanceOf[T]
    }

    def toBean[T: Manifest](clz: Class[T]): T = {
      if (jsonHandler.equals("lift")) {
        net.liftweb.json.parse(str).extract[T]
      } else
        objectMapper.readValue(str, clz)
    }
  }
}
