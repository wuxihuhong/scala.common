package huhong.scala.common.dao

import java.io.Serializable

import huhong.scala.common.hibernate.query.Query
import huhong.scala.navigator.Navigator

import huhong.scala.common.hibernate._

/**
 * Created by huhong on 15/1/15.
 */
trait QueryDaoSupport[T <: Serializable] {
  self: GenericDao[T, _ <: Serializable] =>

  def query(q: Query, page: Int, pageSize: Int): Navigator[T] = {
    val params = q.toParams()
    val countQuery = session.createQuery(q.toCountHQL())




    val query = session().createQuery(q.toActualHql())
    params.foreach(p => {
      query.setParameter(p._1, p._2)
      countQuery.setParameter(p._1, p._2)
    })

    val count = countQuery.count()
    val paperInfo = Navigator.getPageRange(count, page, pageSize)
    val begin = paperInfo.begin
    val end = paperInfo.end
    query.setFirstResult(begin).setMaxResults(end - begin)
    val data = query.list().asInstanceOf[java.util.List[T]]
    Navigator[T](data, 0, begin, end)
  }

  def query(q:Query):java.util.List[T]={
    val query = session().createQuery(q.toActualHql())
    val params = q.toParams()
    params.foreach(p => {
      query.setParameter(p._1, p._2)

    })
    query.list().asInstanceOf[java.util.List[T]]
  }
}
