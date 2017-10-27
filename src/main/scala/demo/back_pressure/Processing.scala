package demo
package back_pressure

import scala.concurrent.{ExecutionContext, Future}

object Processing {

  def toUppercaseAsync(str: String)
                      (implicit ec: ExecutionContext)
  : Future[String] = Future {
    Thread.sleep(500)
    str.toUpperCase
  }

  def toOppositeAsync(i: Int)
                     (implicit ec: ExecutionContext)
  : Future[Int] = Future {
    Thread.sleep(500)
    -i
  }

}
