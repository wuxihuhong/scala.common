package huhong.scala.common.dao

import huhong.scala.common.hibernate.QueryExt
import scala.collection.JavaConverters._
import org.hibernate.{Session, Query}
import java.io.Serializable

trait GenericDao[E <: Serializable, PK <: Serializable] extends SessionSupport {
  def entityCls(): Class[_]

  @throws(classOf[Throwable])
  def list(): List[E] = {

    javaList().asScala.toList
  }

  @throws(classOf[Throwable])
  def list(begin: Int, end: Int): List[E] = javaList(begin, end).asScala.toList

  @throws(classOf[Throwable])
  def javaList(begin: Int, end: Int): java.util.List[E]

  @throws(classOf[Throwable])
  def javaList(): java.util.List[E]

  @throws(classOf[Throwable])
  def save(e: E): E

  @throws(classOf[Throwable])
  def saveOrUpdate(e: E): E

  @throws(classOf[Throwable])
  def update(id: PK, e: E): E = update(id, e, false)

  @throws(classOf[Throwable])
  def update(id: PK, e: E, nullable: Boolean): E

  @throws(classOf[Throwable])
  def update(e: E): E

  @throws(classOf[Throwable])
  def merge(e: E): E

  @throws(classOf[Throwable])
  def merge(id: PK, e: E, nullable: Boolean): E

  @throws(classOf[Throwable])
  def merge(id: PK, e: E): E = merge(id, e, false)

  @throws(classOf[Throwable])
  def delete(e: E): Unit

  @throws(classOf[Throwable])
  def deleteById(id: PK): Unit

  @throws(classOf[Throwable])
  def get(id: PK): E

  @throws(classOf[Throwable])
  def +(data: E): E = save(data)

  @throws(classOf[Throwable])
  def +=(data: E): E = saveOrUpdate(data)

  @throws(classOf[Throwable])
  def ->(data: E): E = update(data)

  @throws(classOf[Throwable])
  def ->(id: PK, data: E): E = update(id, data)

  @throws(classOf[Throwable])
  def -->(id: PK, data: E): E = merge(id, data)

  def -->(data: E): E = merge(data)

  @throws(classOf[Throwable])
  def -(data: E) = delete(data)

  @throws(classOf[Throwable])
  def --(id: PK) = deleteById(id)

  @throws(classOf[Throwable])
  def apply() = list

  @throws(classOf[Throwable])
  def apply(id: PK) = get(id)

  @throws(classOf[Throwable])
  def count(q: Query): Long = q.count()

  @throws(classOf[Throwable])
  def count():Long


  def refresh(data:E):Unit=session().refresh(data)
}