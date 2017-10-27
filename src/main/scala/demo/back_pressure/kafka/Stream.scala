package demo.back_pressure
package kafka

import akka.actor.ActorSystem
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import demo.back_pressure.kafka.common.KafkaSettings

import scala.concurrent.ExecutionContext

object Stream {

  def main(args: Array[String]): Unit =
    kafkaSource
      .map(_.record.value())
      .mapAsync(parallelism)(Processing.toUppercaseAsync)
      .map(elt => ProducerRecord(out, elt))
      .runWith(kafkaSink)

  val in: String = "stream_in"
  val out: String = "stream_out"

  private val kafkaSource = {
    val settings = KafkaSettings.consumerSettings.withGroupId("stream")
    Consumer.committableSource(settings, Subscriptions.topics(in))
  }
  private val kafkaSink = Producer.plainSink(KafkaSettings.producerSettings)

  private val parallelism = Runtime.getRuntime.availableProcessors()

  implicit private val system: ActorSystem = ActorSystem()
  implicit private val ec: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer()

}
