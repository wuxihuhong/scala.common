package huhong.scala.test.dao

import huhong.scala.common.dao.SessionDaoSupport
import huhong.scala.common.dao.impl.BaseGenericDao
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.{Repository, Component}
import huhong.scala.test.domain.User

/**
 * Created by huhong on 14/12/19.
 */
@Repository
class UserDao @Autowired protected(sf: SessionFactory) extends BaseGenericDao[User, String](sf) with SessionDaoSupport {

}
