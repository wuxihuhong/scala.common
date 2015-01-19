package huhong.scala.common.log

import org.slf4j.LoggerFactory

/**
 * Created by huhong on 15/1/19.
 */
trait LogSupport {
  protected lazy val logger = LoggerFactory.getLogger(this.getClass)

  protected def info(msg: String, e: Throwable = null) = {
    if (logger.isInfoEnabled)
      logger.info(msg, e)
  }

  protected def debug(msg: String, e: Throwable = null) = {
    if (logger.isDebugEnabled) {
      logger.debug(msg, e)
    }
  }

  protected def error(msg: String, e: Throwable = null) = {
    if (logger.isErrorEnabled) {
      logger.error(msg, e)
    }
  }

  protected def warn(msg: String, e: Throwable = null) {
    if (logger.isWarnEnabled) {
      logger.warn(msg, e)
    }
  }

  protected def trace(msg: String, e: Throwable = null) = {
    if (logger.isTraceEnabled) {
      logger.trace(msg, e)
    }
  }
}
