package huhong.scala.common.test.domain

import java.util.Date
import javax.persistence.{ManyToOne, OneToMany, Entity}

import org.hibernate.search.annotations._

/**
 * Created by huhong on 15/1/16.
 */
@Entity
@Indexed
class Human extends Serializable {
  @javax.persistence.Id
  @org.hibernate.annotations.GenericGenerator(name = "uuid", strategy = "uuid")
  @javax.persistence.Column(length = 100)
  @javax.persistence.GeneratedValue(generator = "uuid")
  var id: String = _

  @Field
  var name: String = _

  @OneToMany
  @IndexedEmbedded(depth = 2, includePaths = Array(
    "name"
  ))
  var parents: java.util.Set[Human] = _

  @ContainedIn
  @ManyToOne
  var child: Human = _


  @javax.persistence.Column(insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  @org.hibernate.annotations.Generated(value = org.hibernate.annotations.GenerationTime.INSERT)
  @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Field(store=Store.YES)
  var createDate: Date = _
}
