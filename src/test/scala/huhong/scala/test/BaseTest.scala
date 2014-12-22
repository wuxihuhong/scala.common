package huhong.scala.test

import huhong.scala.test.dao.UserDao


import org.junit.Test

import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.{AbstractTransactionalJUnit4SpringContextTests, SpringJUnit4ClassRunner}
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.transaction.annotation.Transactional
import huhong.scala.json._
import huhong.scala._


@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath:beans.xml"))
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
class BaseTest extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired
  var userDao: UserDao = _


  //@Test
  def test {

  }

  @Test
  def hqlTest(): Unit = {
    userDao.withSession {
      implicit session => {
        import huhong.scala.hibernate.hqlBuilder
        val username = ("username" -> List("huhong", "wl"))
        val pwd = "123456"
        val ret = hql"from User u where password=$pwd and u.username in ($username)".list()

        println(ret.toJsonString().formatJsonString())
      }
    }

  }

}