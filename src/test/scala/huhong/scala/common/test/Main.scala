package huhong.scala.common.test

import java.io.StringWriter
import java.util
import java.util.Date

import huhong.scala.test.domain.User

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext
import java.util.{List => JList}
import java.util.{Map => JMap}

/**
 * Created by huhong on 14/12/22.
 */
object Main extends App {

  case class UserScala(var username: String)

  class TestClass {
    @BeanProperty
    var strs: Array[String] = _

    var objs: Array[User] = _

    var lists: List[User] = _

    var seq: Seq[User] = _

    var map: Map[String, Any] = _

    var jlist: JList[User] = _

    var jmap: JMap[String, Any] = _
    var createDate = new Date
  }

  val user = new User
  user.username = "huhong"

  val userScala = new UserScala("huhong2")

  import huhong.scala.common.json._

  jsonHandler = "jackson"

  var start = System.currentTimeMillis()

  user.toJson(System.out)
  userScala.toJson(System.out)

  println(System.currentTimeMillis() - start)

  jsonHandler = "lift"
  start = System.currentTimeMillis()
  user.toJson(System.out)
  userScala.toJson(System.out)
  println(System.currentTimeMillis() - start)


  start = System.currentTimeMillis()
  val lpu =
    """{"username":"huhong2","password":null}""".toBean[User]

  println(System.currentTimeMillis() - start)

  jsonHandler = "jackson"
  val jpu =
    """{"username":"huhong2","password":null}""".toBean[User]
  println(System.currentTimeMillis() - start)


  val tc = new TestClass
  tc.setStrs(Array("a", "b"))
  tc.objs = (Array(jpu))
  tc.lists = List(jpu)
  tc.seq = Seq(jpu)
  tc.map = Map("test" -> jpu)
  tc.jlist = new java.util.ArrayList[User]()
  tc.jlist.add(jpu)
  tc.jmap = new java.util.HashMap[String, Any]
  tc.jmap.put("test2",jpu)
  tc.toJson(System.out)


  val totc="""{"strs":["a","b"],"objs":[{"username":"huhong2"}],"lists":[{"username":"huhong2"}],"seq":[{"username":"huhong2"}],"map":{"test":{"username":"huhong2"}},"jlist":[{"username":"huhong2"}],"jmap":{"test2":{"username":"huhong2"}},"createDate":1420816275702}""".toBean[TestClass]
  println(totc)
  // userScala.toJson(System.out)


  //System.out.println("fuck")
  //  println(System.currentTimeMillis() - start)
  //
  //
  //

  //
  //  println(System.currentTimeMillis() - start)
}
