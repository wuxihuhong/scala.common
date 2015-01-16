package huhong.scala.common.test.domain

import javax.persistence._

import org.hibernate.search.annotations.{IndexedEmbedded, ContainedIn, Field}

/**
 * Created by huhong on 15/1/16.
 */
@Entity
class Address {
  @Id
  @GeneratedValue
  var id: Long = _

  @Field
  var street: String = _

  @Field
  var city: String = _

  @IndexedEmbedded(depth = 1, prefix = "ownedBy_")
  @Embedded
  var ownedBy: Owner = _

  @ContainedIn
  @OneToMany(mappedBy = "address")
  var places: java.util.Set[Place] = _
}
