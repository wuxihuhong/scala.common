package huhong.scala.common.test.domain

import javax.persistence.Embeddable

import org.hibernate.search.annotations.Field

/**
 * Created by huhong on 15/1/16.
 */
@Embeddable
class Owner {
  @Field
  var name:String=_
}
