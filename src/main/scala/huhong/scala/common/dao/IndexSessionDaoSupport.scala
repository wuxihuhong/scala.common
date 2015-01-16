package huhong.scala.common.dao

import java.io.Serializable

import org.hibernate.search.{FullTextSession, Search}

/**
 * Created by huhong on 15/1/16.
 */
trait IndexSessionDaoSupport[T <: Serializable] {
  self: GenericDao[T, _ <: Serializable] =>

  def fullTextSession(): FullTextSession = Search.getFullTextSession(session())

  def indexQueryBuilder()=fullTextSession().getSearchFactory.buildQueryBuilder().forEntity(entityCls()).get()
}
