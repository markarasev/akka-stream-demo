package demo

import demo.common.{KafkaSettings, Processing}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ExplodeV2 {

  def main(args: Array[String]): Unit = {
    val flow = Subscriber.andThen(Processor).andThen(Publisher)
    while (true) flow(())
  }

  object Subscriber extends (Unit => Future[String]) {

    private var buffer = Seq.empty[String]

    private val consumer = {
      val settings = KafkaSettings.consumerSettings.withGroupId("explode_v2")
      settings.createKafkaConsumer()
    }
    consumer.subscribe(Seq.apply(KafkaSettings.topic).asJava)

    private def consume(): Unit = {
      val records = consumer.poll(100)
      val values = records.iterator().asScala.to[Seq]
        .map(_.value())
      buffer = buffer ++ values
    }

    override def apply(v1: Unit): Future[String] =
      buffer.headOption.fold {
        consume()
        apply(())
      } { elt =>
        buffer = buffer.tail
        Future.successful(elt)
      }

  }

  object Processor extends (Future[String] => Future[String]) {

    override def apply(eltF: Future[String]): Future[String] = eltF.flatMap(elt => Processing.toLowercaseAsync(elt))

  }

  val out = "explode_v2_out"

  object Publisher extends (Future[String] => Future[Unit]) {

    private val producer = KafkaSettings.producerSettings.createKafkaProducer()

    override def apply(eltF: Future[String]): Future[Unit] = eltF.flatMap { elt =>
      val record = ProducerRecord.apply(out, elt)
      Future(producer.send(record))
    }

  }

}
