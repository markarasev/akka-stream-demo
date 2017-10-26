package demo
package back_pressure
package kafka

import demo.back_pressure.kafka.common.{KafkaSettings, Processing}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object BufferOverflowOrDropMessages {

  def main(args: Array[String]): Unit = {
    val buffer = new Buffer(500)
    val processor = new Processor(buffer)
    while (true) {
     consume().map(buffer.enqueue)
     processor.process().map(produce)
    }
  }

  private val consumer = KafkaSettings.consumerSettings
    .withGroupId("explode_buffer")
    .createKafkaConsumer()
  consumer.subscribe(Seq.apply(KafkaSettings.topic).asJava)

  private def consume() = {
    val records = consumer.poll(100)
    records.iterator().asScala.to[Seq]
      .map(_.value())
  }

  class Buffer(maxSize: Int) {

    private var underlying = Seq.empty[String]

    def enqueue(elt: String): Try[Unit] =
      if (underlying.size < maxSize) {
        underlying = underlying :+ elt
        Success(())
      }
      else Failure(new Buffer.BufferOverflowException)

    def dequeue(): Try[String] = underlying.headOption.fold[Try[String]](
      Failure(new Buffer.BufferEmptyException)
    ) { elt =>
      underlying = underlying.tail
      Success(elt)
    }

  }

  object Buffer {
    class BufferOverflowException extends RuntimeException
    class BufferEmptyException extends RuntimeException
  }

  class Processor(buffer: Buffer) {

    def process(): Future[String] = Future.fromTry(buffer.dequeue()).flatMap(Processing.toLowercaseAsync)

  }

  val out: String = "explode_buffer_out"

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()

  private def produce(elt: String) = {
    val record = ProducerRecord.apply(out, elt)
    producer.send(record)
  }

}
