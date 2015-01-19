package huhong.scala.common.dao

import java.io.Serializable

import huhong.scala.common.hibernate.query.IndexQuery
import huhong.scala.navigator.Navigator
import org.hibernate.search.{FullTextSession, Search}

/**
 * Created by huhong on 15/1/16.
 */
trait IndexQueryDaoSupport {
  self: SessionSupport =>

  def fullTextSession(): FullTextSession = Search.getFullTextSession(session())

  def indexQueryBuilder[T <: Serializable : Manifest]() = fullTextSession().getSearchFactory.buildQueryBuilder().forEntity(manifest[T].runtimeClass).get()


  def indexQuery[T <: Serializable : Manifest](query: IndexQuery, page: Int, pageSize: Int): Navigator[T] = {
    val luceneQuery = query.toIndexQuery(indexQueryBuilder[T])
    QueryDaoSupport.logger.debug("lucene query:" + luceneQuery.toString())
    val hibQuery = fullTextSession().createFullTextQuery(luceneQuery, manifest[T].runtimeClass)
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