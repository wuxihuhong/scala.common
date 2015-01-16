package huhong.scala.test


import java.util

import huhong.scala.common.test.domain._
import huhong.scala.common.test.{EvalContexts, TestInterface}
import huhong.scala.common.test.dao.{HotelDao, HumanDao, PlaceDao, UserDao}
import huhong.scala.test.domain.User
import org.junit.Test

import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.{AbstractTransactionalJUnit4SpringContextTests, SpringJUnit4ClassRunner}
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.transaction.annotation.Transactional
import huhong.scala.common.json._
import huhong.scala.common._


@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath:beans.xml"))
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
class BaseTest extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired
  var userDao: UserDao = _


  @Autowired
  var placeDao: PlaceDao = _


  @Autowired
  var humanDao: HumanDao = _

  @Autowired
  var hotelDao: HotelDao = _

  //@Test
  def test {
    //    import scala.reflect.runtime._
    //    import scala.tools.reflect.ToolBox
    //    import huhong.scala.common._
    //    val cm = scala.reflect.runtime.universe.runtimeMirror(this.getClass.getClassLoader)
    //    val toolbox = cm.mkToolBox()
    //
    //    val code =
    //      """
    //      huhong.scala.common.test.EvalContexts.result="hi"
    //      """
    //    val codetree = toolbox.parse(code)
    //    toolbox.eval(codetree)
    //
    //    println(EvalContexts.result)


    val user = new User

    user.username = "huhong"

    //userDao + user
    Predef.println(userDao.searchUser("huhong").size())
  }


  def indexTest: Unit = {
    val human = new Human
    human.name = "胡hu"

    humanDao + human
  }

  @Test
  def pointTest: Unit = {
    //    val hotel = new Hotel
    //
    //    hotel.latitude = 31.4891959
    //    hotel.longitude = 120.3077468
    //    hotel.address = "无锡新区"
    //    hotel.name = "无锡中意大酒店"
    //    hotel.rooms=500
    //    hotel.deleted=false
    //
    //
    //    hotelDao + hotel

    import scala.collection.JavaConverters._
    val latitude = 31.4891959
    val longitude = 120.3077468
    Predef.println(hotelDao.search("无锡中意大酒店", "锡新",501) .asScala.toJsonString())
  }

  //@Test
  def hqlTest(): Unit = {
    userDao.withSession {
      implicit session => {
        import huhong.scala.common.hibernate._
        val username = ("username" -> List("huhong", "wl"))
        val pwd = "123456"
        val ret = hql"from User u where password=$pwd and u.username in ($username)".list



        println(ret.toJsonString().formatJsonString())
      }
    }

  }

}