package demo

import demo.common.{KafkaSettings, Process}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Explodes from Xmx400M
object ExplodesDueToMemory {

  def main(args: Array[String]): Unit = while(true) consume().map(Process.toLowercaseAsync).map(produce)

  private val consumer = {
    val settings = KafkaSettings.consumerSettings.withGroupId("explode")
    val consumer = settings.createKafkaConsumer()
    consumer.subscribe(Seq.apply(KafkaSettings.topic).asJava)
    consumer
  }

  private def consume() = {
    val records = consumer.poll(100)
    records.iterator().asScala.to[Seq]
      .map(_.value())
  }

  val out: String = "explode_out"

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()

  private def produce(eltFuture: Future[String]) = eltFuture.map { elt =>
    val record = ProducerRecord.apply(out, elt)
    producer.send(record)
  }

}
