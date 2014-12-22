package huhong.scala.error

class CustomException(msg: String = null, cause: Throwable = null, enableSuppression: Boolean = true, writableStackTrace: Boolean = true)
  extends Exception(msg, cause, enableSuppression, writableStackTrace) {

}