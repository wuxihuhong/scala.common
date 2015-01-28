package huhong.scala.common.spring

import java.lang.annotation.Annotation

import org.springframework.aop.TargetSource
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator

import scala.beans.BeanProperty

/**
 * Created by huhong on 15/1/19.
 */
class AnnotationAutoProxyCreator extends AbstractAutoProxyCreator {

  @BeanProperty
  var annotationClass: Class[_ <: Annotation] = _

  def getAdvicesAndAdvisorsForBean(beanClass: Class[_], beanName: String, customTargetSource: TargetSource): Array[Object] = {
    if (beanClass.isAnnotationPresent(annotationClass)) {
      AbstractAutoProxyCreator.PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS
    } else {
      AbstractAutoProxyCreator.DO_NOT_PROXY
    }
  }
}


abstract class MatchAutoProxyCreator extends AbstractAutoProxyCreator {
  def `match`(beanClass: Class[_], beanName: String, customTargetSource: TargetSource): Boolean

  def getAdvicesAndAdvisorsForBean(beanClass: Class[_], beanName: String, customTargetSource: TargetSource): Array[Object] = {
    if (`match`(beanClass, beanName, customTargetSource)) {
      AbstractAutoProxyCreator.PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS
    } else {
      AbstractAutoProxyCreator.DO_NOT_PROXY
    }
  }
}