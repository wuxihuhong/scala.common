package huhong.scala.common.hibernate.query

/**
 * Created by huhong on 15/2/11.
 */
trait QueryBuilder {
  def createHql(value: Any): QuerySupport
}
