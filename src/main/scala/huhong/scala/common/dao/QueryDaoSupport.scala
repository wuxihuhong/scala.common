package huhong.scala.common.dao

import java.io.Serializable

import huhong.scala.common.hibernate.query.Query
import huhong.scala.navigator.Navigator

import huhong.scala.common.hibernate._
import org.slf4j.LoggerFactory

/**
 * Created by huhong on 15/1/15.
 */
trait QueryDaoSupport {
  self:SessionSupport =>


  def query[T <:Serializable](q: Query, page: Int, pageSize: Int): Navigator[T] = {
    val params = q.toParams()
    val countQuery = session.createQuery(q.toCountHQL())


    if (QueryDaoSupport.logger.isDebugEnabled()) {
      QueryDaoSupport.logger.debug("query count hql:" + q.toCountHQL())
      QueryDaoSupport.logger.debug("query hql:" + q.toActualHql())
    }

    val query = session().createQuery(q.toActualHql())
    params.foreach(p => {
      query.setParameter(p._1, p._2)
      countQuery.setParameter(p._1, p._2)
    })

    val count = countQuery.count()

    if (QueryDaoSupport.logger.isDebugEnabled()) {
      QueryDaoSupport.logger.debug(s"found data size:$count")
    }
    val paperInfo = Navigator.getPageRange(count, page, pageSize)
    val begin = paperInfo.begin
    val end = paperInfo.end
    query.setFirstResult(begin).setMaxResults(end - begin)
    val data = query.list().asInstanceOf[java.util.List[T]]
    Navigator[T](data, count, begin, end)
  }

  def query[T <:Serializable](q: Query): java.util.List[T] = {
    val query = session().createQuery(q.toActualHql())
    val params = q.toParams()
    params.foreach(p => {
      query.setParameter(p._1, p._2)

    })
    query.list().asInstanceOf[java.util.List[T]]
  }
}

object QueryDaoSupport {
  lazy val logger = LoggerFactory.getLogger(classOf[Query])
}