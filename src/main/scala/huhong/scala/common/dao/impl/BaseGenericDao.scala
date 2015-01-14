package huhong.scala.common.dao.impl

import huhong.scala.common.unable_update

import huhong.scala.common.dao.GenericDao
import java.io.Serializable
import org.hibernate.{Session, SessionFactory}
import huhong.scala.common.error.CustomException
import huhong.scala.common.hibernate._

class BaseGenericDao[E <: Serializable : Manifest, PK <: Serializable : Manifest](sf: SessionFactory) extends GenericDao[E, PK] {

  implicit def session() = sf.getCurrentSession

  val entityCls = manifest[E].runtimeClass

  @throws(classOf[Exception])
  def javaList(): java.util.List[E] = {
    session.createCriteria(entityCls).list().asInstanceOf[java.util.List[E]]
  }

  @throws(classOf[Exception])
  def save(e: E): E = {
    session.save(e)
    e
  }

  @throws(classOf[Exception])
  def saveOrUpdate(e: E): E = {
    session.saveOrUpdate(e)
    e
  }

  @throws(classOf[Exception])
  def update(id: PK, e: E, setnull: Boolean): E = {

    val dbobj = this.get(id)

    if (dbobj != null) {
      copyProperties(e, dbobj, setnull)
      update(dbobj)
    } else {
      throw new CustomException("id:" + id + " is null,update fail.")
    }
    e
  }

  @throws(classOf[Exception])
  def update(e: E): E = {
    session.update(e)
    e
  }

  @throws(classOf[Exception])
  def delete(e: E): Unit = session.delete(e)

  @throws(classOf[Exception])
  def deleteById(id: PK): Unit = {
    val dbobj = this.get(id)
    if (dbobj == null) {
      throw new CustomException("id:" + id + " is null,delete fail.")
    } else {
      delete(dbobj)
    }
  }

  @throws(classOf[Exception])
  def get(id: PK): E = {
    session.get(entityCls, id).asInstanceOf[E]
  }

  @throws(classOf[Throwable])
  def javaList(begin: Int, end: Int): java.util.List[E] = {
    session.createCriteria(entityCls).setFirstResult(begin).setMaxResults(end - begin).list().asInstanceOf[java.util.List[E]]
  }

  @throws(classOf[Exception])
  protected def copyProperties(src: Serializable, dist: Serializable, setnull: Boolean = false) = {
    val cls = src.getClass
    cls.getDeclaredFields().foreach(f => {
      if (!f.isAnnotationPresent(classOf[unable_update]) && !f.isAnnotationPresent(classOf[javax.persistence.Id])) {
        f.setAccessible(true)
        val value = f.get(src)
        value match {
          case null => {
            if (setnull) {
              f.set(dist, null)
            }
          }
          case _ => {
            f.set(dist, value)
          }
        }
      }
    })

  }


}