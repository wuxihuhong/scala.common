package huhong.scala.common.dao.impl

import java.io.Serializable

import huhong.scala.common.dao.{IndexQueryDaoSupport, QueryDaoSupport, SessionDaoSupport, SessionSupport}
import huhong.scala.common.error.CustomException
import huhong.scala.common.unable_update
import org.hibernate.{LockOptions, Session, Query, SessionFactory}

import scala.collection.JavaConverters._
import huhong.scala.common.hibernate._

import scala.beans.BeanProperty

/**
 * Created by huhong on 15/1/19.
 */
class CommonDao(@BeanProperty var sf: SessionFactory) extends SessionSupport with SessionDaoSupport with QueryDaoSupport with IndexQueryDaoSupport {

  def this() = this(null)

  @throws(classOf[Throwable])
  def list[E <: Serializable : Manifest](): List[E] = {

    javaList().asScala.toList
  }

  @throws(classOf[Throwable])
  def list[E <: Serializable : Manifest](begin: Int, end: Int): List[E] = javaList(begin, end).asScala.toList

  @throws(classOf[Throwable])
  def javaList[E <: Serializable : Manifest](begin: Int, end: Int): java.util.List[E] = {
    session.createCriteria(manifest[E].runtimeClass).setFirstResult(begin).setMaxResults(end - begin).list().asInstanceOf[java.util.List[E]]
  }

  @throws(classOf[Throwable])
  def javaList[E <: Serializable : Manifest](): java.util.List[E] = {
    session.createCriteria(manifest[E].runtimeClass).list().asInstanceOf[java.util.List[E]]
  }


  @throws(classOf[Throwable])
  def lockJavaList[E <: Serializable : Manifest](begin: Int, end: Int, lockOptions: LockOptions): java.util.List[E] = {
    session.createCriteria(manifest[E].runtimeClass).setLockMode(lockOptions.getLockMode).setFirstResult(begin).setMaxResults(end - begin).list().asInstanceOf[java.util.List[E]]
  }

  @throws(classOf[Throwable])
  def lockJavaList[E <: Serializable : Manifest](lockOptions: LockOptions): java.util.List[E] = {
    session.createCriteria(manifest[E].runtimeClass).setLockMode(lockOptions.getLockMode).list().asInstanceOf[java.util.List[E]]
  }

  @throws(classOf[Throwable])
  def lockList[E <: Serializable : Manifest](begin: Int, end: Int, lockOptions: LockOptions): List[E] = lockJavaList(begin, end, lockOptions).asScala.toList

  @throws(classOf[Throwable])
  def lockList[E <: Serializable : Manifest](lockOptions: LockOptions): List[E] = lockJavaList(lockOptions).asScala.toList

  @throws(classOf[Throwable])
  def save[E <: Serializable](e: E): E = {
    session.save(e)
    e
  }

  @throws(classOf[Throwable])
  def saveOrUpdate[E <: Serializable](e: E): E = {
    session.saveOrUpdate(e)
    e
  }

  @throws(classOf[Throwable])
  def update[PK <: Serializable, E <: Serializable : Manifest](id: PK, e: E): E = update(id, e, false)

  @throws(classOf[Throwable])
  def update[PK <: Serializable, E <: Serializable : Manifest](id: PK, e: E, nullable: Boolean): E = {
    val dbobj = this.get[PK, E](id)

    if (dbobj != null) {
      copyProperties(e, dbobj, nullable)
      update(dbobj)
    } else {
      throw new CustomException("id:" + id + " is null,update fail.")
    }
    e
  }

  @throws(classOf[Throwable])
  def update[E <: Serializable](e: E): E = {
    session().update(e)
    e
  }

  @throws(classOf[Throwable])
  def merge[E <: Serializable](e: E): E = {
    session.merge(e)
    e
  }

  @throws(classOf[Throwable])
  def merge[PK <: Serializable, E <: Serializable : Manifest](id: PK, e: E, nullable: Boolean): E = {
    val dbobj = this.get(id)

    if (dbobj != null) {
      copyProperties(e, dbobj, nullable)
      merge(dbobj)
    } else {
      throw new CustomException("id:" + id + " is null,update fail.")
    }
    e
  }

  @throws(classOf[Throwable])
  def merge[PK <: Serializable, E <: Serializable : Manifest](id: PK, e: E): E = merge(id, e, false)

  @throws(classOf[Throwable])
  def delete[E <: Serializable](e: E): Unit = {
    session.delete(e)
  }

  @throws(classOf[Throwable])
  def deleteById[PK <: Serializable, E <: Serializable : Manifest](id: PK): Unit = {
    val dbobj = this.get[PK, E](id)
    if (dbobj == null) {
      throw new CustomException("id:" + id + " is null,delete fail.")
    } else {
      delete(dbobj)
    }
  }

  @throws(classOf[Throwable])
  def get[PK <: Serializable, E <: Serializable : Manifest](id: PK, lockOptions: LockOptions = null): E = {
    if (lockOptions == null)
      session().get(manifest[E].runtimeClass, id).asInstanceOf[E]
    else
      session().get(manifest[E].runtimeClass, id, lockOptions).asInstanceOf[E]
  }

  @throws(classOf[Throwable])
  def +[E <: Serializable](data: E): E = save[E](data)

  @throws(classOf[Throwable])
  def +=[E <: Serializable](data: E): E = saveOrUpdate[E](data)

  @throws(classOf[Throwable])
  def ->[E <: Serializable](data: E): E = update[E](data)

  @throws(classOf[Throwable])
  def ->[PK <: Serializable, E <: Serializable : Manifest](id: PK, data: E): E = update[PK, E](id, data)

  @throws(classOf[Throwable])
  def -->[PK <: Serializable, E <: Serializable : Manifest](id: PK, data: E): E = merge[PK, E](id, data)

  def -->[E <: Serializable](data: E): E = merge[E](data)

  @throws(classOf[Throwable])
  def -[E <: Serializable](data: E) = delete[E](data)

  @throws(classOf[Throwable])
  def --[PK <: Serializable, E <: Serializable : Manifest](id: PK) = deleteById[PK, E](id)

  @throws(classOf[Throwable])
  def apply[E <: Serializable : Manifest](): List[E] = list[E]

  @throws(classOf[Throwable])
  def apply[PK <: Serializable, E <: Serializable : Manifest](id: PK, lockOptions: LockOptions = null): E = get[PK, E](id, lockOptions)

  @throws(classOf[Throwable])
  def count(q: Query): Long = q.count()


  def session(): Session = sf.getCurrentSession


  def refresh[E <: Serializable](data: E): Unit = session().refresh(data)

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
