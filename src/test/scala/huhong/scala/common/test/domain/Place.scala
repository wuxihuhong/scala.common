package huhong.scala.common.test.domain

import javax.persistence._

import org.hibernate.search.annotations.{IndexedEmbedded, Field, Indexed}

/**
 * Created by huhong on 15/1/16.
 */
@Entity
@Indexed
class Place extends Serializable {
  @Id
  @GeneratedValue
  var id: Long = _

  @Field
  var name: String = _

  @OneToOne(cascade = Array(
    CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.ALL
  ))
  @IndexedEmbedded
  var address: Address = _


}
