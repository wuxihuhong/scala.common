package huhong.scala.common.navigator


import huhong.scala.navigator.Navigator
import net.liftweb.json._
import scala.collection.JavaConversions._

/**
 * Created by huhong on 15/1/28.
 */
case class NavigatorSerializer extends Serializer[Navigator[_]] {
  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Navigator[_]] = ???

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case nav: Navigator[_] => {

      val datas = if (nav.datas != null && nav.datas.size() > 0) {
        nav.datas.map {
          format.customSerializer.apply(_)
        } toList

      } else {
        Nil
      }

      JObject(JField("count", JInt(BigInt(nav.count))) :: JField("page", JInt(BigInt(nav.page))) ::
        JField("pagesize", JInt(BigInt(nav.pagesize))) :: JField("totalPages", JInt(BigInt(nav.totalPages)))
        :: JField("hasNext", JBool(nav.hasNext)) :: JField("hasPrev", JBool(nav.hasPrev))
        :: JField("datas", JArray(datas)) :: Nil)
    }
  }
}
