package demo.back_pressure.kafka.common

import scala.concurrent.{ExecutionContext, Future}

object Processing {

  def toLowercaseAsync(str: String)(implicit ec: ExecutionContext): Future[String] = Future {
    Thread.sleep(500)
    str.toUpperCase
  }

}
