package huhong.scala.common.test

import java.io.{StringReader, StringWriter}
import java.util
import java.util.Date


import huhong.scala.common._
import huhong.scala.common.dao.impl.CommonDao
import huhong.scala.common.lucene.analysis.CharAnalyzer
import huhong.scala.common.test.dao.{HotelDao, UserDao}
import huhong.scala.common.test.domain.Hotel
import huhong.scala.test.domain.User
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.springframework.context.support.ClassPathXmlApplicationContext

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext
import java.util.{List => JList}
import java.util.{Map => JMap}


/**
 * Created by huhong on 14/12/22.
 */
object Main extends App {


  val str = "天气不错132abc@#$+-)(*&^.,/"

  val analyzer = new CharAnalyzer
  val ts = analyzer.tokenStream("test", new StringReader(str))
  val term = ts.addAttribute(classOf[CharTermAttribute])
  ts.reset()

  while (ts.incrementToken()) {
    println(term.toString)
  }
  ts.end()
  ts.close()

  import huhong.scala.common.hibernate.query._

  val ctx = new ClassPathXmlApplicationContext("beans.xml")
  val hd = ctx.getBean(classOf[HotelDao])

  @querytables(tables = Array(new querytable(value = "User", alias = "u")))
  @orderby(value = "order by u.createDate desc")
  @sort(value = "address")
  class UserQuery extends OptionFieldQuery with Serializable {


    @query_field(value = "accout_name", op = "like", tablename = "u")
    var username: Option[String] = None

    //@where("u.username=?")
    @query_field(value = "address", op = "=", tablename = "u")
    var password: Option[String] = None
  }


  import scala.reflect.runtime.universe._
  import scala.reflect.api._
  import scala.reflect.runtime._
  import huhong.scala.common.json._





  val userQuery = new UserQuery
  userQuery.password = Some("123456")
  userQuery.username = Some("%huhong%")
  println(userQuery.toActualHql())
  println(userQuery.toParams())
  println(userQuery.toCountHQL())

  println(userQuery.toIndexQuery(hd.indexQueryBuilder[Hotel]))

  println(userQuery.toIndexSort())
  //  val ret = userQuery.getTypeTag(userQuery).tpe.members.filter(_.typeSignature <:< typeOf[Option[_]])
  //  val mirror = runtimeMirror(this.getClass.getClassLoader)
  //  val instanceMirror = mirror.reflect(userQuery)
  //  ret.foreach(f => {
  //
  //    val found = f.annotations.find(_.tpe =:= typeOf[tablename])
  //
  //    if (found.isDefined)
  //      println(found.get.javaArgs.map { case (name, value) =>
  //        name.toString -> value
  //      }.find(_._1.equals("value")).get._2.toString)
  //
  //  })
  //
  //  println(ret)
  //  val foundAnno = mirror.classSymbol(classOf[UserQuery]).annotations.find(_.tpe.typeConstructor <:< typeOf[querytables])
  //
  //  println(foundAnno)
  //
  //
  //
  //  userQuery.tables.toJson(System.out)

}
