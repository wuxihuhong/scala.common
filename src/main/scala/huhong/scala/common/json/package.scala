package huhong.scala.common

import java.io.{StringWriter, Writer, OutputStream, Serializable}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.{JsonParser, GsonBuilder}

/**
 * Created by huhong on 14/12/18.
 */
package object json {

  private val objectMapper = new ObjectMapper
  objectMapper.registerModule(DefaultScalaModule)

  private lazy val gson = new GsonBuilder().setPrettyPrinting().create()
  private lazy val jsonParser=new JsonParser

  implicit def JsonEx(bean: Any) = new {
    def toJson(out: OutputStream) = {
      objectMapper.writeValue(out, bean)
    }

    def toJson(writer: Writer) = {
      objectMapper.writeValue(writer, bean)
    }


    def toJsonString():String={
      val sw=new StringWriter()
      toJson(sw)
      try{
        sw.toString
      }
      finally{
        sw.close();
      }
    }
  }

  implicit def StringEx(str:String)=new {
    def formatJsonString():String={
      gson.toJson(jsonParser.parse(str))
    }
  }
}
