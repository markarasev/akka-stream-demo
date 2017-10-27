package demo
package back_pressure
package kafka

import demo.back_pressure.kafka.common.KafkaSettings

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Explodes from Xmx400M
object ExplodeMem {

  def main(args: Array[String]): Unit =
    while(true)
      consume()
        .map(Processing.toUppercaseAsync)
        .map(produce)

  val in: String = "explode_mem_in"

  private val consumer = {
    val settings = KafkaSettings.consumerSettings.withGroupId("explode_mem")
    settings.createKafkaConsumer()
  }
  consumer.subscribe(Seq(in).asJava)

  private def consume() = {
    val records = consumer.poll(100)
    records.iterator().asScala.to[Seq]
      .map(_.value())
  }

  val out: String = "explode_mem_out"

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()

  private def produce(eltFuture: Future[String]) = eltFuture.map { elt =>
    val record = ProducerRecord(out, elt)
    producer.send(record)
  }

}
