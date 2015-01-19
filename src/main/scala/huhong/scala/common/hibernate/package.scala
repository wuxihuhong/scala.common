package huhong.scala.common

import java.util.Collection

import org.hibernate.{Query, Session}

import scala.collection.JavaConverters._

/**
 * Created by huhong on 14/12/19.
 */
package object hibernate {

  @inline implicit def hqlBuilder(sc: StringContext)(implicit session: Session) = HqlBuilder(sc)

  case class HqlBuilder(sc: StringContext)(implicit session: Session) {
    def hql(p: Any*) = {
      sc.checkLengths(p)


      val hqlsb = new StringBuilder
      for (i <- 0 until sc.parts.length - 1) {

        p(i) match {
          case (name: String, value: Any) => {
            hqlsb ++= sc.parts(i) + ":" + name
          }
          case _ => {
            hqlsb ++= sc.parts(i) + "?"
          }
        }
      }

      hqlsb ++= sc.parts(sc.parts.length - 1)

      val q = session.createQuery(hqlsb.toString())

      for (i <- 0 until p.length) {
        p(i) match {
          case (s: String, c: Collection[_]) => {
            q.setParameterList(s, c)
          }
          case (s: String, c: Seq[_]) => {
            q.setParameterList(s, c.asJavaCollection)
          }
          case (s: String, c: Array[_]) => {
            q.setParameterList(s, c.toList.asJavaCollection)
          }
          case (s: String, a: Any) => {
            q.setParameter(s, a)
          }
          case _ => {

            q.setParameter(i, p(i).asInstanceOf[AnyRef])
          }

        }
      }

      q
    }


  }

  implicit def QueryExt(q: Query) = new {
    def count() = q.uniqueResult().asInstanceOf[Number].longValue()

    def first(f: Int) = q.setFirstResult(f)

    def max(m: Int) = q.setMaxResults(m)

    def list[T] = q.list().asInstanceOf[java.util.List[T]]

    def list[T](first: Int, max: Int) = {
      q.setFirstResult(first)
      q.setMaxResults(max)
      q.list().asInstanceOf[java.util.List[T]]
    }
  }


}
