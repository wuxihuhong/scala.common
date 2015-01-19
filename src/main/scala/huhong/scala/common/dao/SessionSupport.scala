package huhong.scala.common.dao

import org.hibernate.Session

/**
 * Created by huhong on 15/1/19.
 */
trait SessionSupport {
  def session(): Session
}
