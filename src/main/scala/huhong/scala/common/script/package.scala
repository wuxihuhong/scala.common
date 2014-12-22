package huhong.scala.common

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime._
import scala.tools.reflect.ToolBox

/**
 * Created by huhong on 14/12/22.
 */
package object script {
  private lazy val cm = scala.reflect.runtime.universe.runtimeMirror(this.getClass.getClassLoader)
  private lazy val toolbox = cm.mkToolBox()


  object EvalContext {

    private val params = new ThreadLocal[ConcurrentMap[String, Any]]
    private val results = new ThreadLocal[Any];


    def $[T >: Null](name: String): T = {
      val ps = params.get()
      if (ps != null)
        ps.get(name).asInstanceOf[T]
      else
        null
    }

    def setParam(name: String, value: Any) = {
      val map = if (params.get() == null)
        new ConcurrentHashMap[String, Any]
      else {
        params.get()
      }
      map.put(name, value)
      params.set(map)
    }


    def setResult(ret: Any): Unit = {
      results.set(ret)
    }

    def getResult() = {
      results.get()
    }
  }


  def eval(code: String): Any = {

    val fixedCode =
      s"""
        |import huhong.scala.common.script.EvalContext._
        |setResult({
        |$code
        |})
      """.stripMargin

    val tree = toolbox.parse(fixedCode)
    this.synchronized {
      toolbox.eval(tree)
    }
    EvalContext.getResult()


  }

  def asyncEval(code: String, params: (String, () => Any)*)(implicit ec: ExecutionContext): Future[Any] = {
    Future {

      params.foreach {
        case (name: String, func: (() => Any)) => {
          EvalContext.setParam(name, func())
        }
      }
      val fixedCode =
        s"""
        |import huhong.scala.common.script.EvalContext._
        |setResult({
        |$code
        |})
      """.stripMargin

      val curtoolbox = cm.mkToolBox()
      val tree = curtoolbox.parse(fixedCode)


      curtoolbox.eval(tree)


      EvalContext.getResult()
    }
  }

}
