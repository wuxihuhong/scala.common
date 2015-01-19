package huhong.scala.common

import java.util.ResourceBundle
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by huhong on 15/1/19.
 */
package object resources {


  private val resourcePool: java.util.Map[String, ResourceBundle] = new ConcurrentHashMap[String, ResourceBundle]()

  def get(filename: String, propName: String, defaultValue: => String = null) = {
    val rb = getResourceBundle(filename)
    if (defaultValue != null) {
      if (rb.containsKey(propName)) {
        rb.getString(propName)
      } else {
        defaultValue
      }
    } else {
      rb.getString(propName)
    }
  }


  private def getResourceBundle(filename: String) = {
    if (resourcePool.containsKey(filename))
      resourcePool.get(filename)
    else {
      val bundle = ResourceBundle.getBundle(filename)
      resourcePool.put(filename, bundle)
      bundle
    }
  }
}
