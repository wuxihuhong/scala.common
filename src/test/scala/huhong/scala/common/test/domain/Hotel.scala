package huhong.scala.common.test.domain

import java.lang
import javax.persistence.Entity

import org.hibernate.search.annotations._
import org.hibernate.search.spatial.Coordinates

import scala.beans.BeanProperty

/**
 * Created by huhong on 15/1/16.
 */
@Entity
@Indexed
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

  @Field
  @BeanProperty
  var name: String = _

  @Field
  @BeanProperty
  var address: String = _

  @Spatial(spatialMode = SpatialMode.HASH)
  def getLocation() = {
    new Coordinates {
      def getLongitude: lang.Double = longitude

      def getLatitude: lang.Double = latitude
    }
  }
}
