package huhong.scala.common.dao

import java.io.Serializable

import org.hibernate.Session

/**
 * Created by huhong on 14/12/19.
 */
trait SessionDaoSupport {
  self: SessionSupport =>

  def withSession[R](func: (Session) => R): R = {
    func(this.session())
  }
}
