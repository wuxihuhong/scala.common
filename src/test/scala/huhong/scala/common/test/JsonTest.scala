package huhong.scala.common.test

import java.util
import java.util.Date

import com.google.gson.stream.{JsonToken, JsonWriter, JsonReader}
import com.google.gson.{TypeAdapter, GsonBuilder, Gson}
import huhong.scala.common.json._

/**
 * Created by huhong on 15/1/28.
 */
object JsonTest extends App {


  class TestBean {
    var list: java.util.List[String] = new util.ArrayList[String]()

    var nullvalue: String = _

    var date:Date=new Date()

    list.add("a")

    var name:Option[String]=Some("胡宏")


    var bean:Option[TestBean2]=Some(new TestBean2)
  }


  class TestBean2{

    var shit="shit"
  }


  var start=System.currentTimeMillis()
  println(new TestBean().toJsonString())

  val bean= """{"list":["a"],"nullvalue":null,"date":1422437731253,"name":{"class":"java.lang.String","value":"胡宏"},"bean":{"class":"huhong.scala.common.test.JsonTest$TestBean2","value":{"shit":"shit"}}}""".toBean[TestBean]

  println(System.currentTimeMillis()-start)

  jsonHandler="jackson"
  start=System.currentTimeMillis()

  println(new TestBean().toJsonString())

  """{"list":["a"]}""".toBean[TestBean]

  println(System.currentTimeMillis()-start)
  //  println(new TestBean().toJsonString())
  //
  // val testbean= """{"list":["a"],"nullvalue":null}""".toBean[TestBean]
  //
  //  println(testbean.list.get(0))
}
