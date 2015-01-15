package huhong.scala.navigator

import java.io.Serializable
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


class Navigator[T <: Serializable](@BeanProperty var datas: java.util.List[T], @BeanProperty var count: Long, @BeanProperty var page: Int, @BeanProperty var pagesize: Int) extends Serializable {


  @BeanProperty
  val totalPages = (count - 1) / pagesize + 1

  @BeanProperty
  def hasNext = (pagesize * (page + 1) < count)

  @BeanProperty
  def hasPrev = page > 0

  @BeanProperty
  val next = hasNext

  @BeanProperty
  val prev = hasPrev

  def asScala = new ScalaNavigator(datas.asScala, count, page, pagesize)
}

class ScalaNavigator[T <: Serializable](val datas: Seq[T], val count: Long, val page: Int, val pagesize: Int) extends Serializable {


  val totalPages = (count - 1) / pagesize + 1


  def hasNext = (pagesize * (page + 1) < count)

  def hasPrev = page > 0


  val next = hasNext


  val prev = hasPrev

  def asJava = new Navigator(datas.asJava, count, page, pagesize)
}

object Navigator {
  def getPageRange(count: Long, page: Int, pagesize: Int) = {
    count match {
      case 0L => PageRange(0, pagesize)
      case _ => {
        val begin = page * pagesize
        if (begin > count) {
          PageRange(0, pagesize)
        } else {
          PageRange(begin, begin + pagesize)
        }
      }
    }
  }

  def apply[T <: Serializable](datas: java.util.List[T], count: Long, page: Int, pagesize: Int) = new Navigator[T](datas, count, page, pagesize)

  def apply[T <: Serializable](datas: List[T], count: Long, page: Int, pagesize: Int) = new Navigator[T](datas, count, page, pagesize)
}

case class PageRange(val begin: Int, val end: Int)