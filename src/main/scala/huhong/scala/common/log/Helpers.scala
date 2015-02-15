package huhong.scala.common.log

import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by huhong on 15/1/19.
 */
object Helpers {


  @inline def info(msg: String, e: Throwable = null)(implicit logger: Logger) = {
    if (logger.isInfoEnabled)
      logger.info(msg, e)
  }

  @inline def debug(msg: String, e: Throwable = null)(implicit logger: Logger) = {
    if (logger.isDebugEnabled) {
      logger.debug(msg, e)
    }
  }

  @inline def error(msg: String, e: Throwable = null)(implicit logger: Logger) = {
    if (logger.isErrorEnabled) {
      logger.error(msg, e)
    }
  }

  @inline def warn(msg: String, e: Throwable = null)(implicit logger: Logger) {
    if (logger.isWarnEnabled) {
      logger.warn(msg, e)
    }
  }

  @inline def trace(msg: String, e: Throwable = null)(implicit logger: Logger) = {
    if (logger.isTraceEnabled) {
      logger.trace(msg, e)
    }
  }


}
