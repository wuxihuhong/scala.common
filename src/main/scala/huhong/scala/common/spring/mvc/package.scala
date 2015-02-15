package huhong.scala.common.spring

import javax.servlet.http.HttpServletResponse

/**
 * Created by huhong on 15/2/6.
 */
package object mvc {
  def contentType(contentType: String)(implicit resp: HttpServletResponse) = {
    resp.setContentType(contentType)
  }


}
