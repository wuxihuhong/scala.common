package huhong.scala.common.test.dao

import javax.transaction.Transactional

import huhong.scala.common.dao.{IndexQueryDaoSupport, SessionDaoSupport}
import huhong.scala.common.dao.impl.BaseGenericDao
import huhong.scala.common.test.domain.{Hotel, Human}
import huhong.scala.test.domain.User
import org.apache.lucene.index.Term
import org.apache.lucene.search.{PrefixQuery, TermQuery, PhraseQuery, Sort}
import org.hibernate.SessionFactory
import org.hibernate.search.bridge.StringBridge
import org.hibernate.search.{FullTextQuery, Search}
import org.hibernate.search.spatial.{DistanceSortField, Coordinates}
import org.hibernate.search.spatial.impl.Point
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.hibernate.search.query.dsl.{PhraseTermination, Unit}

/**
 * Created by huhong on 15/1/16.
 */
@Repository
@Transactional
class HotelDao @Autowired protected(sf: SessionFactory) extends BaseGenericDao[Hotel, String](sf) with SessionDaoSupport with IndexQueryDaoSupport[Hotel] {
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


  def search(name: String, address: String, maxRoom: Int) = {
    val fullTextSession = Search.getFullTextSession(session())
    val qb = fullTextSession.getSearchFactory.buildQueryBuilder().forEntity(classOf[Hotel]).get()


    //val q = qb.bool().must(createFullMatchingQuery("name", name)).should(new TermQuery(new Term("name", name))).must(qb.keyword().onField("address").matching(address).createQuery()).createQuery()
    val q = qb.bool().must(qb.keyword().onField("name").matching(name).createQuery()).must(createFullMatchingQuery("address", address)).must(
      qb.range().onField("rooms").from(0).to(500).createQuery()
    ).must(qb.keyword().onField("deleted").matching("true").createQuery()).createQuery()
    fullTextSession.createFullTextQuery(q, classOf[Hotel]).list()

  }

  def createFullMatchingQuery(field: String, value: String) = {
    val pq = new PhraseQuery
    value.foreach {
      c => {
        pq.add(new Term(field, c.toString))
        pq.setSlop(0)
      }
    }

    pq

    //    new TermQuery(new Term(field, value));


  }
}
