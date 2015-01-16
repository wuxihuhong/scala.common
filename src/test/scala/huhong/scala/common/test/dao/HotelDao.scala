package huhong.scala.common.test.dao

import javax.transaction.Transactional

import huhong.scala.common.dao.SessionDaoSupport
import huhong.scala.common.dao.impl.BaseGenericDao
import huhong.scala.common.test.domain.{Hotel, Human}
import huhong.scala.test.domain.User
import org.apache.lucene.search.Sort
import org.hibernate.SessionFactory
import org.hibernate.search.{FullTextQuery, Search}
import org.hibernate.search.spatial.{DistanceSortField, Coordinates}
import org.hibernate.search.spatial.impl.Point
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.hibernate.search.query.dsl.Unit

/**
 * Created by huhong on 15/1/16.
 */
@Repository
@Transactional
class HotelDao @Autowired protected(sf: SessionFactory) extends BaseGenericDao[Hotel, String](sf) with SessionDaoSupport {
  def searchBy(latitude: Double, longitude: Double) = {
    val coordinates = Point.fromDegrees(latitude, longitude)
    val fullTextSession = Search.getFullTextSession(session())
    val qb = fullTextSession.getSearchFactory.buildQueryBuilder().forEntity(classOf[Hotel]).get()
    val q = qb.spatial().onField("location").within(20, Unit.KM).ofLatitude(latitude).andLongitude(longitude).createQuery()
    val hibQuery = fullTextSession.createFullTextQuery(q, classOf[Hotel])
    hibQuery.setSort(new Sort(
      new DistanceSortField(latitude, longitude, "location")))

    hibQuery.list()
  }


  def search(name:String,address:String)={
    val fullTextSession = Search.getFullTextSession(session())
    val qb = fullTextSession.getSearchFactory.buildQueryBuilder().forEntity(classOf[Hotel]).get()

  }


}
