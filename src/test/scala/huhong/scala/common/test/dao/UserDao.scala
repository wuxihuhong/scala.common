package huhong.scala.common.test.dao

import javax.transaction.Transactional

import huhong.scala.common.dao.{IndexQueryDaoSupport, SessionDaoSupport}
import huhong.scala.common.dao.impl.BaseGenericDao
import org.hibernate.SessionFactory
import org.hibernate.search.Search
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.{Repository, Component}
import huhong.scala.test.domain.User

/**
 * Created by huhong on 14/12/19.
 */
@Repository
@Transactional
class UserDao @Autowired protected(sf: SessionFactory) extends BaseGenericDao[User, String](sf)
with SessionDaoSupport{


  def startIndex() {
    val fullTextSession = Search.getFullTextSession(session())
    fullTextSession.createIndexer().startAndWait()
  }


  def afterPropertiesSet(): Unit = {
    //startIndex()
  }


  //  public List<Book> searchForBook(String searchText) throws Exception
  //  {
  //    try
  //    {
  //      Session session = mySessionFactory.getCurrentSession();
  //
  //      FullTextSession fullTextSession = Search.getFullTextSession(session);
  //
  //      QueryBuilder qb = fullTextSession.getSearchFactory()
  //        .buildQueryBuilder().forEntity(Book.class).get();
  //    org.apache.lucene.search.Query query = qb
  //      .keyword().onFields("description", "title", "author")
  //      .matching(searchText)
  //      .createQuery();
  //
  //    org.hibernate.Query hibQuery =
  //      fullTextSession.createFullTextQuery(query, Book.class);
  //
  //    List<Book> results = hibQuery.list();
  //    return results;
  //    }
  //    catch(Exception e)
  //    {
  //      throw e;
  //    }
  //  }

  def searchUser(username: String) = {
    val fullTextSession = Search.getFullTextSession(session())
    val qb = fullTextSession.getSearchFactory.buildQueryBuilder().forEntity(classOf[User]).get()
    val q = qb.keyword().onField("username").matching(username).createQuery()

    fullTextSession.createFullTextQuery(q, classOf[User]).list()


  }
}
