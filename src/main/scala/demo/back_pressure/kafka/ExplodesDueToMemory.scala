package demo
package back_pressure
package kafka

import demo.back_pressure.kafka.common.{KafkaSettings, Processing}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Explodes from Xmx400M
object ExplodesDueToMemory {

  def main(args: Array[String]): Unit = while(true) consume().map(Processing.toLowercaseAsync).map(produce)

  private val consumer = {
    val settings = KafkaSettings.consumerSettings.withGroupId("explode_mem")
    settings.createKafkaConsumer()
  }
  consumer.subscribe(Seq.apply(KafkaSettings.topic).asJava)

  private def consume() = {
    val records = consumer.poll(100)
    records.iterator().asScala.to[Seq]
      .map(_.value())
  }

  val out: String = "explode_mem_out"

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()

  private def produce(eltFuture: Future[String]) = eltFuture.map { elt =>
    val record = ProducerRecord.apply(out, elt)
    producer.send(record)
  }

}
