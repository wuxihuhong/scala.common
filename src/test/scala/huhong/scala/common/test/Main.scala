package huhong.scala.common.test

import scala.concurrent.ExecutionContext

/**
 * Created by huhong on 14/12/22.
 */
object Main extends App {

  case class UserInfo(val username: String);

  import huhong.scala.common._

  import huhong.scala.common.script._


  val code =
    """
      |println("hello "+${"name"})
    """.stripMargin


  import scala.concurrent.ops._

  implicit val ec = ExecutionContext.global
  for (i <- 0 until 993000) {

    asyncEval(code, "name" -> (() => {
      Thread.currentThread().getName
    }))

  }


  System.in.read()


  def getName() = {

  }
}
