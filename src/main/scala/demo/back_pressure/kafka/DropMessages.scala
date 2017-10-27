package demo
package back_pressure
package kafka

import demo.back_pressure.kafka.common.KafkaSettings

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object DropMessages {

  def main(args: Array[String]): Unit = {
    val buffer = new Buffer(500)
    while (true) {
      consume().foreach(buffer.enqueue)
      buffer.dequeue()
        .map { elt =>
          val processingFuture = Processing.toUppercaseAsync(elt)
          processingFuture.map(produce)
        }
    }
  }

  val in: String = "explode_buffer_in"

  private val consumer = KafkaSettings.consumerSettings
    .withGroupId("explode_buffer")
    .createKafkaConsumer()
  consumer.subscribe(Seq(in).asJava)

  private def consume() = {
    val records = consumer.poll(100)
    records.iterator().asScala.to[Seq]
      .map(_.value())
  }

  class Buffer(maxSize: Int) {

    private var underlying = Seq.empty[String]
    private var nDropped = 0

    def enqueue(elt: String): Unit =
      if (underlying.size < maxSize)
        underlying = underlying :+ elt
      else {
        nDropped += 1
        println(s"dropped $nDropped elements, last: $elt")
      }

    def dequeue(): Option[String] = underlying.headOption.map { elt =>
      underlying = underlying.tail
      elt
    }

  }

  val out: String = "explode_buffer_out"

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()

  private def produce(elt: String) = {
    val record = ProducerRecord(out, elt)
    producer.send(record)
  }

}
