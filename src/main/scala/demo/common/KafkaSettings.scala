package demo.common

import akka.kafka.{ConsumerSettings, ProducerSettings}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

object KafkaSettings {

  val topic: String = "in"

  private val bootstrapServers = "localhost:9092"

  val consumerSettings: ConsumerSettings[Array[Byte], String] = {
    val config = ConfigFactory.load("consumer.conf")
    ConsumerSettings.apply(config, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  }

  val producerSettings: ProducerSettings[Array[Byte], String] = {
    val config = ConfigFactory.load("producer.conf")
    ProducerSettings.apply(config, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)
  }

}
