package huhong.scala.common.error

import scala.beans.BeanProperty

class CustomException( msg: String = null, cause: Throwable = null)
  extends Exception(msg, cause) {

}
