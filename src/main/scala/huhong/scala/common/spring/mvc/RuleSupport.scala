package huhong.scala.common.spring.mvc

import javax.servlet.http.HttpSession

import huhong.scala.common.error.CustomException
import org.springframework.web.servlet.ModelAndView

import scala.collection.mutable.ListBuffer

/**
 * Created by huhong on 14/12/22.
 */
class RuleSupport {

  class RuleException(msg: String) extends CustomException(msg)


  implicit def ModelAndViewEx(mav: ModelAndView) = new {

    def setStatus(value: Symbol)(implicit name: String, session: HttpSession): ModelAndView = {
      session.setAttribute(name, value)
      mav
    }

    def getStatus(implicit name: String, session: HttpSession): Symbol = {
      session.getAttribute(name).asInstanceOf[Symbol]
    }

    def status(value: Symbol)(implicit name: String, session: HttpSession): ModelAndView = {
      setStatus(value);
    }

    def status(implicit name: String, session: HttpSession): Symbol = {
      getStatus
    }

    def clearStatus(immediately: Boolean = false)(implicit name: String, session: HttpSession): ModelAndView = {
      if (immediately)
        session.setAttribute(name, null)
      else
        session.setAttribute(name, 'todo_clear)
      mav
    }

    def withSession(name: String, value: Any)(implicit session: HttpSession): ModelAndView = {
      session.setAttribute(name, value)
      mav
    }
  }

  implicit def toModelAndView(view: String) = new ModelAndView(view)

  implicit def toModelAndView(viewStatus: (String, Symbol))(implicit name: String, session: HttpSession) = new ModelAndView(viewStatus._1).setStatus(viewStatus._2)

  case class Success(implicit name: String, session: HttpSession) extends ModelAndView {
    this.setStatus('success)
  }

  case class Failure(view: String)(implicit name: String, session: HttpSession) extends ModelAndView(view) {
    this.setStatus('fail)
  }

  case class Redirect(status: Symbol, url: String, end: Boolean = false)(implicit name: String, session: HttpSession) extends ModelAndView {
    this.setStatus(status)
    this.setViewName(s"redirect:$url")
    if (end) {
      this.clearStatus(true)
    }
  }

  case class Begin(view: String)(implicit name: String, session: HttpSession) extends ModelAndView(view) {
    this.setStatus('begin)
  }

  case class End(view: String)(implicit name: String, session: HttpSession) extends ModelAndView(view) {

    this.clearStatus(true)
  }


  def setStatus(status: Symbol)(implicit name: String, session: HttpSession) = {
    session.setAttribute(name, status)
  }

  def clearStatus(implicit name: String, session: HttpSession) = {
    session.setAttribute(name, null)
  }

  def rules(complateStatus: Symbol, defaultStatus: Symbol = 'begin)(checks: PartialFunction[Symbol, ModelAndView])(implicit sessionName: String, session: HttpSession): ModelAndView = {

    if (!checks.isDefinedAt(complateStatus)) {
      assert(false, s"must define handler:$complateStatus")
    }

    var curstatus = if (session.getAttribute(sessionName) == null) defaultStatus else session.getAttribute(sessionName).asInstanceOf[Symbol]

    if (curstatus == 'todo_clear) curstatus = defaultStatus;


    var mv: ModelAndView = null;

    val dones = ListBuffer[Symbol]()
    while (curstatus != complateStatus && curstatus != 'todo_clear && checks.isDefinedAt(curstatus) && !dones.exists(_ == curstatus)) {

      val oldstatus = curstatus;
      mv = checks(curstatus)

      curstatus = mv.getStatus
      dones += oldstatus
    }

    if (curstatus == complateStatus) {
      mv = checks(curstatus)
    }

    if (mv == null) {
      throw new RuleException(s"not define status:$curstatus")
    } else {
      if (mv.getStatus == 'todo_clear) {

        session.setAttribute(sessionName, null)
      }
    }


    mv;

  }
}
