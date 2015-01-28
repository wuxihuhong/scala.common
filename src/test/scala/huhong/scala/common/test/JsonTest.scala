package huhong.scala.common.test

import java.util

import com.google.gson.{GsonBuilder, Gson}
import huhong.scala.common.json._

/**
 * Created by huhong on 15/1/28.
 */
object JsonTest extends App {


  class TestBean {
    var list: java.util.List[String] = new util.ArrayList[String]()

    var nullvalue: String = _

    list.add("a")
  }


  val gson = new GsonBuilder().serializeNulls().create()

  var start=System.currentTimeMillis()
  println(gson.toJson(new TestBean))

  gson.fromJson( """{"list":["a"]}""", classOf[TestBean])

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
