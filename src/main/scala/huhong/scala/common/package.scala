package huhong.scala

//import pl.project13.scala.rainbow.Rainbow._

/**
 * Created by huhong on 14/12/21.
 */
package object common {
  def println(anys: Any*): Unit = Predef.println(Console.GREEN_B + Console.BLACK + anys.mkString + Console.RESET)


}
