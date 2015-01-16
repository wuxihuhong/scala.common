package huhong.scala.common.test.dao

import javax.transaction.Transactional

import huhong.scala.common.dao.SessionDaoSupport
import huhong.scala.common.dao.impl.BaseGenericDao
import huhong.scala.common.test.domain.Human
import huhong.scala.test.domain.User
import org.hibernate.SessionFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

/**
 * Created by huhong on 15/1/16.
 */
@Repository
@Transactional
class HumanDao @Autowired protected(sf: SessionFactory) extends BaseGenericDao[Human, String](sf) with SessionDaoSupport {

}
