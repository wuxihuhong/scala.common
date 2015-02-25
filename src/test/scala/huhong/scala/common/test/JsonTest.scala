package huhong.scala.common.test

import java.util
import java.util.Date

import com.google.gson.stream.{JsonToken, JsonWriter, JsonReader}
import com.google.gson.{TypeAdapter, GsonBuilder, Gson}
import huhong.scala.common.hibernate.query.OptionFieldQuery
import huhong.scala.common.json._
import huhong.scala.common.{query_field, orderby, querytable, querytables}

/**
 * Created by huhong on 15/1/28.
 */
object JsonTest extends App {


  class TestBean {
    var list: java.util.List[String] = new util.ArrayList[String]()

    var nullvalue: String = _

    var date:Date=new Date()

    list.add("a")

    var name:Option[Date]=Some(new Date())


    var bean:Option[TestBean2]=Some(new TestBean2)
  }


  class TestBean2{

    var shit="shit"
  }


  @querytables(tables = Array(new querytable(value = "com.parking.core.domain.Employee", alias = "e")))
  @orderby(value="e.createDate desc")
  class EmployeeQuery extends OptionFieldQuery {

    var name: Option[String] = None

    var sex: Option[Array[String]] = None

    var birthdayYear: Option[Integer] = None

    var birthdayMonth: Option[Integer] = None

    var hometown: Option[String] = None

    var maritalStatus: Option[Int] = None

    var tel: Option[String] = None

    @query_field(op = "like")
    var address: Option[String] = None

    var workNO: Option[String] = None

    @query_field(value = "job.id")
    var job: Option[String] = None

    var joinDate: Option[Date] = None

    var deleted: Option[Boolean] = Some(false)
  }

  var start=System.currentTimeMillis()

  val e=new EmployeeQuery()
  e.sex=Some(Array("1","2"))
  println(e.toJsonString())

  val bean= """{"name":{},"sex":{"class":"[Ljava.lang.String;","value":["木樨园","体育公园1"]},"birthdayYear":{},"birthdayMonth":{},"hometown":{},"maritalStatus":{},"tel":{},"address":{},"workNO":{},"job":{},"joinDate":{},"deleted":{"class":"java.lang.Boolean","value":false}}""".toBean[EmployeeQuery]
  println(bean.sex.get(0))
  println(System.currentTimeMillis()-start)


  // val testbean= """{"list":["a"],"nullvalue":null}""".toBean[TestBean]
  //
  //  println(testbean.list.get(0))
}
