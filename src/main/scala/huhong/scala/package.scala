package huhong

import pl.project13.scala.rainbow.Rainbow._

/**
 * Created by huhong on 14/12/21.
 */
package object scala {
  def println(anys: Any*): Unit = println((anys mkString).onGreen.white)


}
