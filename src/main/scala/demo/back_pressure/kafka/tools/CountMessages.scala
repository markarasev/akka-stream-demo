package demo.back_pressure.kafka.tools

import demo.back_pressure.kafka.BufferOverflowOrDropMessages
import demo.back_pressure.kafka.common.KafkaSettings

import collection.JavaConverters._

object CountMessages extends App {

  private val consumer = {
    val settings = KafkaSettings.consumerSettings.withGroupId("count")
    settings.createKafkaConsumer()
  }
  consumer.subscribe(Seq.apply(BufferOverflowOrDropMessages.out).asJava)
//  consumer.subscribe(Seq.apply(KafkaSettings.topic).asJava)
//  consumer.subscribe(Seq.apply(ExplodesDueToMemory.out).asJava)

  private var count = 0
  while (true) {
    val records = consumer.poll(100)
    count += records.count()
    if (records.count() != 0) println(s"Total consumed messages: $count. Last: ${records.iterator().asScala.to[Seq].last.value()}")
  }

}
