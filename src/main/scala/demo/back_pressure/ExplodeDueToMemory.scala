package demo
package back_pressure

import scala.concurrent.ExecutionContext.Implicits.global

// Explodes from -Xmx400M
object ExplodeDueToMemory {

  def main(args: Array[String]): Unit = {
    var i = 0
    while(true) {
      i += 1
      val processingFuture = Processing.toOppositeAsync(i)
      processingFuture.foreach(println)
    }
  }

}
