package demo.back_pressure
package kafka

import akka.actor.ActorSystem
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import demo.back_pressure.kafka.common.{KafkaSettings, Processing}

import scala.concurrent.ExecutionContext

object Stream {

  def main(args: Array[String]): Unit =
    kafkaSource
      .map(_.record.value())
      .mapAsync(Runtime.getRuntime.availableProcessors())(Processing.toLowercaseAsync)
      .map(elt => ProducerRecord.apply("stream_out", elt))
      .runWith(kafkaSink)

  private val kafkaSource = {
    val settings = KafkaSettings.consumerSettings.withGroupId("stream")
    Consumer.committableSource(settings, Subscriptions.topics(KafkaSettings.topic))
  }
  private val kafkaSink = Producer.plainSink(KafkaSettings.producerSettings)

  implicit private val system: ActorSystem = ActorSystem.apply()
  implicit private val ec: ExecutionContext = system.dispatcher
  implicit private val materializer: ActorMaterializer = ActorMaterializer.apply()

}
