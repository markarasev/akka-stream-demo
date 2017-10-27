package demo
package back_pressure
package kafka
package tools

import demo.back_pressure.kafka.common.KafkaSettings
import org.scalacheck.Gen

object ProduceMessages extends App {

  private val producer = KafkaSettings.producerSettings.createKafkaProducer()
  private val stringGenerator = Gen.alphaNumStr.suchThat(_.nonEmpty).map(_.toLowerCase)

  private def generateString(): String = stringGenerator.sample.getOrElse(generateString())

  for (i <- 1 to 1000000) {
    val elt = generateString()
    val explodeMemRecord = ProducerRecord(ExplodeMem.in, elt)
    val streamRecord = ProducerRecord(Stream.in, elt)
    val dropMessagesRecord = ProducerRecord(DropMessages.in, elt)
    Seq(explodeMemRecord, streamRecord).foreach(producer.send)
    if (i % 100 == 0) producer.send(dropMessagesRecord)
    if (i % 100000 == 0) println(s"Generated $i messages.")
  }

}
