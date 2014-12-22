package huhong.scala.dao

import java.io.Serializable

import org.hibernate.Session

/**
 * Created by huhong on 14/12/19.
 */
trait SessionDaoSupport {
  self: GenericDao[_ <: Serializable, _ <: Serializable] =>

  def withSession[R](func: (Session) => R): R = {
    func(this.session())
  }
}
