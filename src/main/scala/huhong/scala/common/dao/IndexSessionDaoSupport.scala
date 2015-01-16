package huhong.scala.common.dao

import java.io.Serializable

import huhong.scala.common.hibernate.query.IndexQuery
import huhong.scala.navigator.Navigator
import org.hibernate.search.{FullTextSession, Search}

/**
 * Created by huhong on 15/1/16.
 */
trait IndexSessionDaoSupport[T <: Serializable] {
  self: GenericDao[T, _ <: Serializable] =>

  def fullTextSession(): FullTextSession = Search.getFullTextSession(session())

  def indexQueryBuilder() = fullTextSession().getSearchFactory.buildQueryBuilder().forEntity(entityCls()).get()


}


trait IndexQueryDaoSupport[T <: Serializable] {
  self: GenericDao[T, _ <: Serializable] =>

  def fullTextSession(): FullTextSession = Search.getFullTextSession(session())

  def indexQueryBuilder() = fullTextSession().getSearchFactory.buildQueryBuilder().forEntity(entityCls()).get()


  def indexQuery(query: IndexQuery, page: Int, pageSize: Int): Navigator[T] = {
    val hibQuery = fullTextSession().createFullTextQuery(query.toIndexQuery(indexQueryBuilder), entityCls())
    val count = hibQuery.getResultSize
    val paperInfo = Navigator.getPageRange(count, page, pageSize)
    val begin = paperInfo.begin
    val end = paperInfo.end
    hibQuery.setFirstResult(begin).setMaxResults(end - begin)
    val sort = query.toIndexSort()
    if (sort != null) hibQuery.setSort(sort)
    val datas = hibQuery.list().asInstanceOf[java.util.List[T]]
    Navigator[T](datas, count, page, pageSize)
  }


}