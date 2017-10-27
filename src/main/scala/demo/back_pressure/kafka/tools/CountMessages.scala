package demo
package back_pressure
package kafka
package tools

import demo.back_pressure.kafka.common.KafkaSettings

import scala.collection.JavaConverters._

object CountMessages extends App {

  private val consumer = {
    val settings = KafkaSettings.consumerSettings.withGroupId("count")
    settings.createKafkaConsumer()
  }
  private val topics = Seq(
    Stream.in
    ,ExplodeMem.in
    ,DropMessages.in
    ,Stream.out
    ,ExplodeMem.out
    ,DropMessages.out
  )
  consumer.subscribe(topics.asJava)

  private var count = 0
  while (true) {
    val records = consumer.poll(100)
    count += records.count()
    if (records.count() != 0) {
      val last = records.iterator().asScala.to[Seq].last.value()
      println(s"Total consumed messages: $count. Last: $last")
    }
  }

}
