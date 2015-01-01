package huhong.scala.test.domain

import javax.persistence.{Column, Entity}
import java.util.Date

import scala.beans.BeanProperty

/**
 * Created by huhong on 14/12/19.
 */
@Entity
class User extends java.io.Serializable {
  @javax.persistence.Id
  @org.hibernate.annotations.GenericGenerator(name = "uuid", strategy = "uuid")
  @javax.persistence.Column(length = 100)
  @javax.persistence.GeneratedValue(generator = "uuid")
  var id: String = _

  var username: String = _

  var password: String = _

  @javax.persistence.Column(insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  @org.hibernate.annotations.Generated(value = org.hibernate.annotations.GenerationTime.INSERT)
  @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
  var createDate: Date = _


  var address: String = _


  var tel: String = _

  var email: String = _

  @Column(name = "`desc`")
  var desc: String = _

}
