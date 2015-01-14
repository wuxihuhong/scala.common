package huhong.scala.common.hibernate

import java.util.{Collection => JCollection}


import huhong.scala.common.{where, or, query_field, querytables}
import org.springframework.util.StringUtils

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


/**
 * Created by huhong on 15/1/12.
 */
package object query {


  trait HQLSupport {
    def toHQL(): String;

    var expLink: String = ""
  }

  trait ParametersSupport {
    def toParams(): Seq[(String, Any)];
  }

  type QuerySupport = HQLSupport with ParametersSupport

  abstract class Query extends HQLSupport with ParametersSupport {
    def tables: Seq[QueryTable]

    def queryFields: QueryFields;


  }


  abstract class OptionFieldQuery extends Query {


    def queryFields: QueryFields = createQueryFields()

    def tables: Seq[QueryTable] = {

      if (this.getClass.isAnnotationPresent(classOf[querytables])) {
        val qts = this.getClass.getAnnotation(classOf[querytables])
        qts.tables().map {
          qt => {
            QueryTable(qt.value(), qt.alias())
          }
        }
      }
      else {
        Seq(QueryTable(this.getClass.getName))
      }


    }

    def toHQL(): String = {
      val tableHql = tables.map(_.toHQL()) mkString (",")

      val condHql = queryFields.toHQL()

      s"from $tableHql where $condHql"
    }


    def toParams(): Seq[(String, Any)] = {
      queryFields.toParams()
    }

    protected def createQueryFields() = {
      val clz = this.getClass
      val fields = clz.getDeclaredFields.filter(f => {
        f.getType.isAssignableFrom(classOf[Option[_]])
      })


      var qfs: QueryFields = null

      fields map (f => {

        f.setAccessible(true)
        val valueOpt = f.get(this).asInstanceOf[Option[_]]
        if (valueOpt.isDefined) {

          if (f.isAnnotationPresent(classOf[where])) {
            val where = f.getAnnotation(classOf[where])
            val qs = WhereQuery(where.value(), ":" + f.getName, valueOpt.get)
            if (qfs == null) {
              qfs = new QueryFields(qs.asInstanceOf[QuerySupport])

            } else {
              if (f.isAnnotationPresent(classOf[or])) {
                qfs.or(qs.asInstanceOf[QuerySupport])
              } else {
                qfs.and(qs.asInstanceOf[QuerySupport])
              }
            }
          } else {
            val tablename = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).tablename() else null
            var fieldname = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).value() else f.getName
            if (fieldname.equals("")) {
              fieldname = f.getName
            }
            val op = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).op() else "="

            val paramName = ":" + f.getName
            if (qfs == null) {
              qfs = QueryFields(fieldname, valueOpt.get, tablename, paramName, op)
            } else {
              if (f.isAnnotationPresent(classOf[or])) {
                qfs.or(fieldname, valueOpt.get, tablename, paramName, op)
              } else {
                qfs.and(fieldname, valueOpt.get, tablename, paramName, op)
              }
            }
          }
        }

      })

      qfs
    }
  }


  case class QueryTable(name: String, aliases: String = null) extends Serializable with HQLSupport {
    def toHQL(): String = if (StringUtils.isEmpty(aliases)) name else s"$name $aliases"
  }

  def table(name: String, aliases: String = null) = QueryTable(name, aliases)

  def queryfield(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "=") = QueryFields(name, conditionValue, tableName, paramName, operator)

  private case class QueryField(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "=") extends Serializable with HQLSupport with ParametersSupport {


    if (paramName != null && !paramName.startsWith(":")) assert(false, "参数名卡头必须是:")
    if (operator.toLowerCase().equals("in")) {
      if (!conditionValue.isInstanceOf[JCollection[_]] && !conditionValue.isInstanceOf[Array[_]] ||
        !conditionValue.isInstanceOf[Seq[_]]) {
        assert(false, "in表达式必须使用一个列表条件")
      }

      if (paramName != null) {
        assert(false, "in表达式必须制定参数名")
      }
    }

    def toHQL(): String = {
      val tn = if (StringUtils.isEmpty(tableName)) "" else s"$tableName."


      val hql = operator.toLowerCase() match {
        case "in" => {
          s"$name in ($paramName)"
        }
        case _ => {
          if (paramName != null)
            s"$name $operator $paramName"
          else
            s"$name $operator ?"
        }
      }


      tn + hql
    }

    def toParams(): Seq[(String, Any)] = Seq(((if (paramName == null) s":$name" else paramName), conditionValue))


  }

  private case class WhereQuery(hql: String, paramName: String, conditionValue: Any) extends Serializable with HQLSupport with ParametersSupport {

    if (!paramName.startsWith(":")) {
      assert(false, "参数名卡头必须是:")
    }

    def toHQL(): String = hql.replace("?", paramName)

    def toParams(): Seq[(String, Any)] = Seq((paramName, conditionValue))
  }

  case class QueryFields(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "=") extends Serializable with HQLSupport with ParametersSupport {

    private var conditions: List[QuerySupport] = Nil

    def this() = {
      this(null, null)
    }

    def this(querySupport: QuerySupport) = {
      this(null, null)
      conditions = conditions ::: List(querySupport)
    }

    if (name != null && conditionValue != null)
      conditions = conditions ::: List(QueryField(name, conditionValue, tableName, paramName, operator))

    def or(querySupport: QuerySupport) = {
      if (conditions.length > 0)
        conditions.last.expLink = "or"
      conditions = conditions ::: List(querySupport)
      this
    }

    def and(querySupport: QuerySupport) = {
      if (conditions.length > 0)
        conditions.last.expLink = "and"
      conditions = conditions ::: List(querySupport)
      this
    }

    def or(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "="): QueryFields = {
      if (conditions.length > 0)
        conditions.last.expLink = "or"
      conditions = conditions ::: List(QueryField(name, conditionValue, tableName, paramName, operator))
      this
    }


    def and(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "="): QueryFields = {
      if (conditions.length > 0)
        conditions.last.expLink = "and"
      conditions = conditions ::: List(QueryField(name, conditionValue, tableName, paramName, operator))
      this
    }

    def toHQL(): String = {
      conditions map {
        c => {
          c.toHQL() + " " + c.expLink + " "
        }
      } mkString
    }

    def toParams(): Seq[(String, Any)] = {
      conditions.map(_.toParams().head)
    }
  }


}
