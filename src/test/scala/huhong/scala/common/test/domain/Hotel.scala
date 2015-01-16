package huhong.scala.common.test.domain

import java.lang
import java.util.Date
import javax.persistence.Entity


import org.hibernate.search.annotations._
import org.hibernate.search.spatial.Coordinates

import scala.beans.BeanProperty

/**
 * Created by huhong on 15/1/16.
 */
@Entity
@Indexed
@Analyzer
class Hotel extends Serializable {
  @javax.persistence.Id
  @org.hibernate.annotations.GenericGenerator(name = "uuid", strategy = "uuid")
  @javax.persistence.Column(length = 100)
  @javax.persistence.GeneratedValue(generator = "uuid")
  var id: String = _


  @BeanProperty
  var latitude: Double = _


  @BeanProperty
  var longitude: Double = _

  @Field(analyze = org.hibernate.search.annotations.Analyze.NO)
  @BeanProperty
  var name: String = _

  @Field
  @BeanProperty
  var address: String = _

  @Field
  @BeanProperty
  var deleted: Boolean = _

  @javax.persistence.Column(insertable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
  @org.hibernate.annotations.Generated(value = org.hibernate.annotations.GenerationTime.INSERT)
  @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
  @Field
  var createDate:Date=_

  @Field
  var rooms:Integer=_

  @Spatial(spatialMode = SpatialMode.HASH)
  def getLocation() = {
    new Coordinates {
      def getLongitude: lang.Double = longitude

      def getLatitude: lang.Double = latitude
    }
  }
}
