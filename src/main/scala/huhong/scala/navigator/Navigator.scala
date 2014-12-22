package huhong.scala.navigator

import java.io.Serializable
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


class Navigator[T <: Serializable](val datas: java.util.List[T], val count: Long, val page: Int, val pagesize: Int) extends Serializable {

  def this(datas: List[T], count: Long, page: Int, pagesize: Int) = this(datas.asJava, count, page, pagesize)

  val totalPage = ((count + pagesize - 1) / pagesize).toInt

  def isNext = (pagesize + 1) * page >= count

  def isPrev = page != 0
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