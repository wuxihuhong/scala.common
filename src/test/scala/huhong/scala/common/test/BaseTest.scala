package huhong.scala.test


import huhong.scala.common.test.{EvalContexts, TestInterface}
import huhong.scala.common.test.dao.UserDao
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


  @Test
  def test {
    import scala.reflect.runtime._
    import scala.tools.reflect.ToolBox
    import huhong.scala.common._
    val cm = scala.reflect.runtime.universe.runtimeMirror(this.getClass.getClassLoader)
    val toolbox = cm.mkToolBox()

    val code =
      """
      huhong.scala.common.test.EvalContexts.result="hi"
      """
    val codetree = toolbox.parse(code)
    toolbox.eval(codetree)

    println(EvalContexts.result)

  }

  //@Test
  def hqlTest(): Unit = {
    userDao.withSession {
      implicit session => {
        import huhong.scala.common.hibernate.hqlBuilder
        val username = ("username" -> List("huhong", "wl"))
        val pwd = "123456"
        val ret = hql"from User u where password=$pwd and u.username in ($username)".list()

        println(ret.toJsonString().formatJsonString())
      }
    }

  }

}