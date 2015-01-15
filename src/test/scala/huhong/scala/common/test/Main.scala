package huhong.scala.common.test

import java.io.StringWriter
import java.util
import java.util.Date


import huhong.scala.common._
import huhong.scala.test.domain.User

import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext
import java.util.{List => JList}
import java.util.{Map => JMap}


/**
 * Created by huhong on 14/12/22.
 */
object Main extends App {

  import huhong.scala.common.hibernate.query._


  @querytables(tables = Array(new querytable(value = "User", alias = "u")))
  @orderby(value="order by u.createDate desc")
  class UserQuery extends OptionFieldQuery {


    @query_field(value = "accout_name", op = "like", tablename = "u")
    var username: Option[String] = None

    @where("u.username=?")
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
