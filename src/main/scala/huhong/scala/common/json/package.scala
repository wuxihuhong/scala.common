package huhong.scala.common

import java.io.{StringWriter, Writer, OutputStream, Serializable}
import java.lang.reflect.Type
import java.util.Date

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.JsonParser
import com.google.gson.stream.{JsonToken, JsonReader, JsonWriter}
import com.google.gson._
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

  private class LongToDateAdapter extends TypeAdapter[Date] {
    def write(out: JsonWriter, value: Date): Unit = {
      this.synchronized {
        if (value == null) {
          out.nullValue()

        } else {
          out.value(value.getTime)
        }
      }
    }

    def read(in: JsonReader): Date = {
      if (in.peek eq JsonToken.NULL) {
        in.nextNull
        null
      } else {
        new Date(in.nextLong())
      }
    }
  }


  class OptionSerializer extends JsonSerializer[Option[_]] with JsonDeserializer[Option[_]] {
    def serialize(src: Option[_], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val jsonObject = new JsonObject
      if (src.isDefined) {
        def value = src.get
        jsonObject.addProperty("class", value.asInstanceOf[Object].getClass.getName)

        jsonObject.add("value", context.serialize(value))
      }
      jsonObject
    }

    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Option[_] = {
      if (json.isJsonNull) {
        None
      } else if (json.isJsonObject && json.getAsJsonObject.entrySet().size() == 0) {
        None
      } else {
        val className = json.getAsJsonObject.get("class").getAsString



        val deserialized = context.deserialize(json.getAsJsonObject.get("value"), Class.forName(className)).asInstanceOf[Any]
        Option(deserialized)
      }
    }
  }

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


  lazy val gson = new GsonBuilder().serializeNulls().registerTypeAdapter(classOf[Option[_]], new OptionSerializer()).registerTypeAdapter(classOf[Date], new LongToDateAdapter).create()

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


  def jField(name:String,value:Any)={
    value match {
      case null=>{
        JField(name,JNull)
      }
      case str:String=>{
        JField(name,JString(str))
      }
      case int:Int=>{
        JField(name,JInt(BigInt(int)))
      }
      case int:java.lang.Integer=>{
        JField(name,JInt(BigInt(int)))
      }
      case long:Long=>{
        JField(name,JInt(BigInt(long)))
      }
      case long:java.lang.Long=>{
        JField(name,JInt(BigInt(long)))
      }
      case boolean:Boolean=>{
        JField(name,JBool(boolean))
      }
      case boolean:java.lang.Boolean=>{
        JField(name,JBool(boolean))
      }
      case double:Double=>{
        JField(name,JDouble(double))
      }
      case double:java.lang.Double=>{
        JField(name,JDouble(double))
      }
      case bigDecimal:java.math.BigDecimal=>{
        JField(name,JDouble(bigDecimal.doubleValue()))
      }
      case bigDecimal:BigDecimal=>{
        JField(name,JDouble(bigDecimal.doubleValue()))
      }
      case date:Date=>{
        JField(name,JInt(BigInt(date.getTime)))
      }

    }
  }
}
