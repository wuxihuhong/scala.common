package huhong.scala.common.hibernate

import java.util.{Collection => JCollection, Date}


import huhong.scala.common._
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.hibernate.search.query.dsl.QueryBuilder
import org.springframework.util.StringUtils
import org.apache.lucene.search.{Query => LuceneQuery, _}
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

  trait Query {
    def tables: Seq[QueryTable]

    def queryFields: QueryFields

    def toCountHQL(): String = {
      val tablename = if (StringUtils.isEmpty(tables.head.aliases)) tables.head.name else tables.head.aliases
      val hql = s"select count($tablename) " + toHQL()
      hql
    }


    def toActualHql(): String = {
      val hql = toHQL()
      hql.trim + " " + toOrderByHql()
    }

    def toOrderByHql(): String = {
      val clz = this.getClass
      if (clz.isAnnotationPresent(classOf[orderby])) {
        val orderbyHql = clz.getAnnotation(classOf[orderby]).value().trim()
        if (orderbyHql.startsWith("order by")) {
          orderbyHql
        } else {
          s"order by $orderbyHql"
        }
      } else {
        ""
      }
    }

    def toHQL(): String

    def toParams(): Seq[(String, Any)]
  }

  trait IndexQuery {
    def toIndexQuery(qb: QueryBuilder): LuceneQuery

    def toIndexSort(): Sort
  }

  abstract class OptionFieldQuery extends Query with IndexQuery {


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
            val qs = WhereQuery(where.value(), f.getName, valueOpt.get)
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
            var tablename = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).tablename() else null
            var fieldname = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).value() else f.getName
            if (fieldname.equals("")) {
              fieldname = f.getName
            }
            if (StringUtils.isEmpty(tablename)) {
              val tableDefine = tables

              if (StringUtils.isEmpty(tableDefine(0).aliases)) {
                tablename = tableDefine(0).name
              } else {
                tablename = tableDefine(0).aliases
              }
            }
            val op = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).op() else "="

            val paramName = f.getName
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


    def toIndexQuery(qb: QueryBuilder): LuceneQuery = {
      val clz = this.getClass

      val fields = clz.getDeclaredFields.filter(f => {
        f.getType.isAssignableFrom(classOf[Option[_]])
      })

      var luceneQuery: BooleanQuery = null

      fields foreach (f => {

        f.setAccessible(true)
        val valueOpt = f.get(this).asInstanceOf[Option[_]]
        if (valueOpt.isDefined) {

          if (f.isAnnotationPresent(classOf[where])) {
            val where = f.getAnnotation(classOf[where])


            val qp = if (where.analyzerImpl().getName.equals(classOf[Void].getName)) {
              new QueryParser(f.getName, new org.apache.lucene.analysis.standard.StandardAnalyzer)
            } else {
              new QueryParser(f.getName, where.analyzerImpl().newInstance().asInstanceOf[Analyzer])
            }


            val q = qp.parse(where.value())

            if (f.isAnnotationPresent(classOf[or])) {
              luceneQuery.add(q, BooleanClause.Occur.SHOULD)
            }
            else if (f.isAnnotationPresent(classOf[not])) {
              luceneQuery.add(q, BooleanClause.Occur.MUST_NOT)
            }
            else {
              luceneQuery.add(q, BooleanClause.Occur.MUST)
            }
          } else {

            var fieldname = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).value() else f.getName
            if (fieldname.equals("")) {
              fieldname = f.getName
            }

            val op = if (f.isAnnotationPresent(classOf[query_field])) f.getAnnotation(classOf[query_field]).op() else "="


            if (luceneQuery == null) {
              luceneQuery = new BooleanQuery()
              if (f.isAnnotationPresent(classOf[or])) {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.SHOULD)
              }
              else if (f.isAnnotationPresent(classOf[not])) {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.MUST_NOT)
              }
              else {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.MUST)
              }
            } else {
              if (f.isAnnotationPresent(classOf[or])) {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.SHOULD)
              }
              else if (f.isAnnotationPresent(classOf[not])) {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.MUST_NOT)
              }
              else {
                luceneQuery.add(createChildLuceneQuery(fieldname, valueOpt.get, op, qb), BooleanClause.Occur.MUST)
              }
            }
          }
        }

      })
      luceneQuery
    }

    def toIndexSort(): Sort = {

      if (this.getClass.isAnnotationPresent(classOf[sorts])) {
        val sortinfos = this.getClass.getAnnotation(classOf[sorts])
        if (sortinfos.value().length == 0) {
          new Sort
        } else {
          val sortFields = sortinfos.value().map(sortinfo => {
            new SortField(sortinfo.value(), sortinfo.sortType(), sortinfo.reverse())
          })
          new Sort(sortFields: _*)
        }
      }

      else if (this.getClass.isAnnotationPresent(classOf[sort])) {
        val sortinfo = this.getClass.getAnnotation(classOf[sort])

        new Sort(new SortField(sortinfo.value(), sortinfo.sortType(), sortinfo.reverse()))
      } else {
        null
      }
    }


    private def createChildLuceneQuery(fieldName: String, value: Any, op: String, qb: QueryBuilder): LuceneQuery = {
      value match {
        case str: String if (op.equals("like")) => {
          createPhraseQuery(fieldName, str)
        }
        case str: String if (op.equals("wildcard")) => {
          qb.keyword().wildcard().onField(fieldName).matching(str).createQuery()
        }
        case str: String if (op.equals("=")) => {
          qb.keyword().onField(fieldName).matching(str).createQuery()
        }
        case number: Number if (op.equals("=")) => {
          qb.range().onField(fieldName).from(number).to(number).createQuery()
        }
        case number: Number if (op.equals("<")) => {
          qb.range().onField(fieldName).below(number).createQuery()
        }
        case number: Number if (op.equals(">")) => {
          qb.range().onField(fieldName).above(number).createQuery()
        }
        case number: Number if (op.equals("<=")) => {
          qb.range().onField(fieldName).below(number).excludeLimit().createQuery()
        }
        case number: Number if (op.equals(">=")) => {
          qb.range().onField(fieldName).above(number).excludeLimit().createQuery()
        }
        case date: Date if (op.equals("<")) => {
          qb.range().onField(fieldName).below(date).createQuery()
        }
        case date: Date if (op.equals(">")) => {
          qb.range().onField(fieldName).above(date).createQuery()
        }
        case date: Date if (op.equals("<=")) => {
          qb.range().onField(fieldName).below(date).excludeLimit().createQuery()
        }
        case date: Date if (op.equals(">=")) => {
          qb.range().onField(fieldName).above(date).excludeLimit().createQuery()
        }
        case date: Date if (op.equals("=")) => {
          qb.range().onField(fieldName).from(date).to(date).createQuery()
        }
        case boolean: Boolean => {
          qb.keyword().onField(fieldName).matching(boolean.toString).createQuery()
        }
        case boolean: java.lang.Boolean => {
          qb.keyword().onField(fieldName).matching(boolean.toString).createQuery()
        }
      }
    }


    private def createPhraseQuery(field: String, value: String, slop: Int = 0) = {
      val pq = new PhraseQuery
      value.foreach {
        c => {
          pq.add(new Term(field, c.toString))
          pq.setSlop(slop)
        }
      }
      pq
    }


  }


  case class QueryTable(name: String, aliases: String = null) extends Serializable with HQLSupport {
    def toHQL(): String = if (StringUtils.isEmpty(aliases)) name else s"$name $aliases"
  }

  def table(name: String, aliases: String = null) = QueryTable(name, aliases)

  def queryfield(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "=") = QueryFields(name, conditionValue, tableName, paramName, operator)

  private case class QueryField(name: String, conditionValue: Any, tableName: String = null, paramName: String = null, operator: String = "=") extends Serializable with HQLSupport with ParametersSupport {


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
          s"$name in (:$paramName)"
        }
        case _ => {
          if (paramName != null)
            s"$name $operator :$paramName"
          else
            s"$name $operator ?"
        }
      }


      tn + hql
    }

    def toParams(): Seq[(String, Any)] = Seq(((if (paramName == null) s"$name" else paramName), conditionValue))


  }

  private case class WhereQuery(hql: String, paramName: String, conditionValue: Any) extends Serializable with HQLSupport with ParametersSupport {


    def toHQL(): String = hql.replace("?", s":$paramName")

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
