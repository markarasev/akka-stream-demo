package demo

import akka.actor.ActorSystem
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import demo.common.{KafkaSettings, Process}

import scala.concurrent.ExecutionContext

object Stream {

  def main(args: Array[String]): Unit =
    kafkaSource
      .map(_.record.value())
      .mapAsync(Runtime.getRuntime.availableProcessors())(Process.toLowercaseAsync)
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
